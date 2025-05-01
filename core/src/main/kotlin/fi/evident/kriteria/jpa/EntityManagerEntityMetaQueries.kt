package fi.evident.kriteria.jpa

import fi.evident.kriteria.expression.*
import jakarta.persistence.EntityManager

/**
 * Counts the number of entities that match the given predicate.
 */
public fun <T : Any> EntityManager.countByEntity(
    entityMeta: EntityMeta<T, *>,
    predicate: context(KrExpressionContext) (KrRoot<T>) -> KrPredicate
): Long =
    buildQuery<Long> {
        val entity = from(entityMeta)
        where(predicate(entity))
        select(count())
    }.singleResult

/**
 * Find all entities matching the given predicate.
 *
 * This is a convenience method for a common case where sub-queries etc. are not needed.
 * For a more flexible version, use [buildQuery].
 */
public fun <T : Any> EntityManager.findAllByEntity(
    entityMeta: EntityMeta<T, *>,
    distinct: Boolean = false,
    order: (context(KrOrderByContext) KrRoot<T>.() -> KrOrder)? = null,
    predicate: context(KrExpressionContext) (KrRoot<T>) -> KrPredicate
): List<T> =
    findAll(entityMeta.entityClass) {
        val entity = from(entityMeta)
        where(predicate(entity))
        select(entity, distinct = distinct)

        if (order != null)
            orderBy(order(entity))
    }

/**
 * Finds at most one entity matching the given predicate.
 */
public fun <T : Any> EntityManager.findFirstOrNullByEntity(
    entityMeta: EntityMeta<T, *>,
    order: (context(KrOrderByContext) KrPath<T>.() -> KrOrder)? = null,
    predicate: context(KrExpressionContext) (KrRoot<T>) -> KrPredicate
): T? =
    buildQuery(entityMeta.entityClass) {
        val entity = from(entityMeta)
        where(predicate(entity))
        select(entity)

        if (order != null)
            orderBy(order(entity))
    }
        .setMaxResults(1)
        .resultList
        .firstOrNull()

/**
 * Checks if any entity exists that matches the given predicate for the specified entity type.
 */
public fun <T : Any> EntityManager.containsEntity(
    entityMeta: EntityMeta<T, *>,
    predicate: context(KrExpressionContext) (KrRoot<T>) -> KrPredicate
): Boolean =
    buildQuery<Int> {
        val entity = from(entityMeta)
        where(predicate(entity))
        select(literal(1))
    }
        .setMaxResults(1)
        .resultList.isNotEmpty()
