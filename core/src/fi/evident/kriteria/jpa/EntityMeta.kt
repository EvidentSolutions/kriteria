package fi.evident.kriteria.jpa

import fi.evident.kriteria.utils.receiverClass
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Typed metadata about different entities.
 *
 * Intended to be used as the base class of companion objects of entities.
 */
public abstract class EntityMeta<E : Any, I : Any>(
    public val idProperty: KProperty1<E, I>,
) {

    public val entityClass: KClass<E>
        get() = idProperty.receiverClass

    public val identifierClass: KClass<I>
        @Suppress("UNCHECKED_CAST")
        get() = idProperty.returnType.classifier as KClass<I>? ?: error("unknown type: ${idProperty.returnType}")

    override fun toString(): String = entityClass.simpleName ?: entityClass.qualifiedName ?: entityClass.toString()
}
