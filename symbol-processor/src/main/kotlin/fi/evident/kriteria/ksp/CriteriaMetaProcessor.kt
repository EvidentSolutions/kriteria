package fi.evident.kriteria.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import fi.evident.kriteria.annotations.DelicateCriteriaApi
import fi.evident.kriteria.expression.*
import jakarta.persistence.*

/**
 * Find entities and creates typed Path-accessors for them.
 */
private class CriteriaMetaProcessor(
    private val codeGenerator: CodeGenerator,
    private val packageName: String,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val optInType = ClassName("kotlin", "OptIn")
    private val delicateApiType = DelicateCriteriaApi::class.asClassName()

    override fun process(resolver: Resolver): List<KSAnnotated> = context(KnownTypes(resolver)) {
        val entityClasses = resolver.getClassesWithAnnotation<Entity>()
        val mappedSuperclassClasses = resolver.getClassesWithAnnotation<MappedSuperclass>()
        val embeddableClasses = resolver.getClassesWithAnnotation<Embeddable>()
        val allClasses = entityClasses + mappedSuperclassClasses + embeddableClasses

        for (classDeclaration in allClasses) {
            val className = (classDeclaration.qualifiedName ?: classDeclaration.simpleName).asString()
            try {
                generateCriteriaMeta(classDeclaration).writeTo(codeGenerator, aggregating = false)
            } catch (e: ProcessingException) {
                logger.error("Error when processing class $className: ${e.message}", classDeclaration)
            } catch (e: Exception) {
                logger.error("Unexpected error when processing class $className: $e", classDeclaration)
            }
        }

        return emptyList()
    }

    context(knownTypes: KnownTypes)
    private fun generateCriteriaMeta(classDeclaration: KSClassDeclaration): FileSpec {
        val className = classDeclaration.simpleName.asString()
        val file = FileSpec.builder(packageName, className + "CriteriaMeta")
        file.addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember("%S, %S, %S", "unused", "RedundantVisibilityModifier", "REDUNDANT_VISIBILITY_MODIFIER")
                .build()
        )
        file.addAnnotation(AnnotationSpec.builder(optInType).addMember("%T::class", delicateApiType).build())
        file.addFileComment("Generated by ${javaClass.simpleName} - do not modify")

        logger.info("Processing entity: $className")

        for (property in classDeclaration.getAllProperties()) {
            val propertyName = property.qualifiedName?.asString()
            try {
                processProperty(classDeclaration, property, file)
            } catch (e: ProcessingException) {
                logger.error("Error when processing $propertyName: ${e.message}", property)
            } catch (e: Exception) {
                logger.error("Unexpected error when processing $propertyName: $e", property)
            }
        }

        return file.build()
    }

    context(knownTypes: KnownTypes)
    private fun processProperty(
        classDeclaration: KSClassDeclaration,
        property: KSPropertyDeclaration,
        file: FileSpec.Builder,
    ) {
        if (!property.validate()) return
        if (!property.isPersistentProperty) return

        val resolvedType = property.type.resolve()
        val kind = when {
            property.hasAnnotation<OneToOne>() -> PropertyKind.ONE_TO_ONE
            property.hasAnnotation<ManyToOne>() -> PropertyKind.MANY_TO_ONE

            property.hasAnnotation<ManyToMany>() ->
                when {
                    resolveCollectionElementType(resolvedType) != null -> PropertyKind.MANY_TO_MANY
                    resolveMapParameters(resolvedType) != null -> PropertyKind.MANY_TO_MANY_MAP
                    else -> null
                }

            property.hasAnnotation<OneToMany>() ->
                when {
                    resolveCollectionElementType(resolvedType) != null -> PropertyKind.ONE_TO_MANY
                    resolveMapParameters(resolvedType) != null -> PropertyKind.ONE_TO_MANY_MAP
                    else -> null
                }

            else -> PropertyKind.BASIC
        } ?: return

        processProperty(classDeclaration, property, resolvedType, file, kind)

        // Process many-to-one and one-to-one properties both normally and as basic properties
        if (kind == PropertyKind.MANY_TO_ONE || kind == PropertyKind.ONE_TO_ONE)
            processProperty(classDeclaration, property, resolvedType, file, PropertyKind.BASIC)
    }

    context(knownTypes: KnownTypes)
    private fun processProperty(
        classDeclaration: KSClassDeclaration,
        property: KSPropertyDeclaration,
        resolvedType: KSType,
        file: FileSpec.Builder,
        kind: PropertyKind,
    ) {
        val propertyName = property.simpleName.asString()
        val entityNameSimple = classDeclaration.simpleName.asString()

        val visibility = if (classDeclaration.isPublic()) KModifier.PUBLIC else KModifier.INTERNAL

        val propertyType = kind.createPropertyType(resolvedType)
        val receiverType = kind.createReceiverType(classDeclaration)

        val property = PropertySpec.builder(
            name = propertyName,
            type = propertyType,
        )
            .addModifiers(visibility)
            .addKdoc("Path accessor for [%L.%L].", entityNameSimple, propertyName)
            .receiver(receiverType)
            .getter(
                FunSpec.getterBuilder()
                    .addAnnotation(
                        AnnotationSpec.builder(JvmName::class)
                            .addMember("name = %S", "get${entityNameSimple}_${propertyName}").build()
                    )
                    .addStatement("return ${kind.resolverFunction}(%S)", propertyName).build()
            )

        val containingFile = classDeclaration.containingFile
        if (containingFile != null)
            property.addOriginatingKSFile(containingFile)

        file.addProperty(property.build())
    }
}

private enum class PropertyKind(val resolverFunction: String) {
    BASIC("getBasicUnsafe"),
    MANY_TO_ONE("getManyToOneUnsafe"),
    ONE_TO_ONE("getOneToOneUnsafe"),
    ONE_TO_MANY("getCollectionReferenceUnsafe"),
    ONE_TO_MANY_MAP("getMapReferenceUnsafe"),
    MANY_TO_MANY("getCollectionReferenceUnsafe"),
    MANY_TO_MANY_MAP("getMapReferenceUnsafe");

    context(knownTypes: KnownTypes)
    fun createPropertyType(type: KSType): TypeName = when (this) {
        BASIC -> basicChildType.parameterizedBy(type.makeNotNullable().toTypeName())
        MANY_TO_ONE -> manyToOneChildType.parameterizedBy(type.makeNotNullable().toTypeName())
        ONE_TO_ONE -> oneToOneChildType.parameterizedBy(type.makeNotNullable().toTypeName())
        ONE_TO_MANY -> {
            val elementType = resolveCollectionElementType(type) ?: throw ProcessingException("expected collection type, got: $type")
            collectionReferenceType.parameterizedBy(elementType)
        }
        MANY_TO_MANY -> {
            val elementType = resolveCollectionElementType(type) ?: throw ProcessingException("expected collection type, got: $type")
            collectionReferenceType.parameterizedBy(elementType)
        }
        ONE_TO_MANY_MAP -> {
            val (key, value) = resolveMapParameters(type) ?: throw ProcessingException("expected map type, got: $type")
            mapReferenceType.parameterizedBy(key, value)
        }
        MANY_TO_MANY_MAP -> {
            val (key, value) = resolveMapParameters(type) ?: throw ProcessingException("expected map type, got: $type")
            mapReferenceType.parameterizedBy(key, value)
        }
    }

    fun createReceiverType(entityClass: KSClassDeclaration): TypeName {
        val typeName = WildcardTypeName.producerOf(entityClass.asType(emptyList()).toTypeName())
        return if (this == BASIC) pathType.parameterizedBy(typeName) else fromType.parameterizedBy(STAR, typeName)
    }

    companion object {
        private val pathType = KrPath::class.asClassName()
        private val fromType = KrFrom::class.asClassName()
        private val basicChildType = KrPropertyRef::class.asClassName()
        private val manyToOneChildType = KrManyToOneRef::class.asClassName()
        private val oneToOneChildType = KrOneToOneRef::class.asClassName()
        private val collectionReferenceType = KrCollectionRef::class.asClassName()
        private val mapReferenceType = KrMapRef::class.asClassName()
    }
}

context(knownTypes: KnownTypes)
private fun resolveCollectionElementType(type: KSType): TypeName? {
    val type = type.makeNotNullable()

    if (knownTypes.collectionType.asStarProjectedType().isAssignableFrom(type)) {
        if (type.arguments.size != 1)
            throw ProcessingException("expected collection type with 1 argument, got: $type")

        return type.arguments.first().type!!.resolve().toTypeName()
    }

    return null
}

context(knownTypes: KnownTypes)
private fun resolveMapParameters(type: KSType): Pair<TypeName, TypeName>? {
    val type = type.makeNotNullable()

    if (knownTypes.mapType.asStarProjectedType().isAssignableFrom(type)) {
        if (type.arguments.size != 2)
            throw ProcessingException("expected map type with 2 arguments, got: $type")

        val (key, value) = type.arguments.map { it.type!!.resolve().toTypeName() }
        return key to value
    }

    return null
}

private class KnownTypes(resolver: Resolver) {
    val collectionType = resolver.findRequiredClassDeclarationByName<Collection<*>>()
    val mapType = resolver.findRequiredClassDeclarationByName<Map<*, *>>()
}

private inline fun <reified T> Resolver.findRequiredClassDeclarationByName(): KSClassDeclaration =
    getClassDeclarationByName<T>() ?: throw ProcessingException("Failed to resolve class ${T::class.qualifiedName}")

class CriteriaMetaProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        CriteriaMetaProcessor(
            codeGenerator = environment.codeGenerator,
            packageName = ProcessorConfiguration(environment.options).targetPackage,
            logger = environment.logger
        )
}

private class ProcessingException(message: String) : RuntimeException(message)
