package fi.evident.kriteria.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import jakarta.persistence.Transient
import kotlin.reflect.KClass

internal inline fun <reified T : Annotation> Resolver.getClassesWithAnnotation(): List<KSClassDeclaration> =
    getClassesWithAnnotation(T::class)

internal inline fun <reified T : Annotation> Resolver.getFunctionsWithAnnotation(): List<KSFunctionDeclaration> =
    getFunctionsWithAnnotation(T::class)

private fun Resolver.getSymbolsWithAnnotation(cl: KClass<out Annotation>): Sequence<KSAnnotated> =
    getSymbolsWithAnnotation(cl.qualifiedName ?: error("no qualified name for annotation $cl"))

internal fun Resolver.getClassesWithAnnotation(cl: KClass<out Annotation>): List<KSClassDeclaration> =
    getSymbolsWithAnnotation(cl)
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.validate() }
        .toList()

internal fun Resolver.getFunctionsWithAnnotation(cl: KClass<out Annotation>): List<KSFunctionDeclaration> =
    getSymbolsWithAnnotation(cl)
        .filterIsInstance<KSFunctionDeclaration>()
        .filter { it.validate() }
        .toList()

internal val KSPropertyDeclaration.isPersistentProperty: Boolean
    get() = hasBackingField && !isDelegated() && !hasAnnotation<Transient>()

internal inline fun <reified T : Annotation> KSPropertyDeclaration.hasAnnotation(): Boolean =
    hasAnnotation(T::class)

internal fun KSPropertyDeclaration.hasAnnotation(cl: KClass<out Annotation>): Boolean =
    hasAnnotation(cl.simpleName, cl.qualifiedName)

private fun KSPropertyDeclaration.hasAnnotation(simpleName: String?, qualifiedName: String?): Boolean =
    annotations.any {
        it.shortName.asString() == simpleName &&
                it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName
    }
