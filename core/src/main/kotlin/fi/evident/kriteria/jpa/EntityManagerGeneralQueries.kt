package fi.evident.kriteria.jpa

import fi.evident.kriteria.expression.KrExpressionContext
import fi.evident.kriteria.expression.KrOrderByContext
import fi.evident.kriteria.expression.withDefaultContexts
import fi.evident.kriteria.jpa.translation.translateQuery
import fi.evident.kriteria.query.KrQueryBuilder
import fi.evident.kriteria.query.KrSubQueryContext
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import kotlin.reflect.KClass

/**
 * Constructs a new query. Prefer higher level [findAllByEntity], [findFirstOrNullByEntity] etc. when possible.
 */
public inline fun <reified T : Any> EntityManager.buildQuery(
    noinline callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): TypedQuery<T> =
    buildQuery(T::class, callback)

/**
 * Constructs a new query. Prefer higher level [findAllByEntity], [findFirstOrNullByEntity] etc. when possible.
 */
public fun <T : Any> EntityManager.buildQuery(
    cl: KClass<T>,
    callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): TypedQuery<T> {
    val queryBuilder = KrQueryBuilder<T>()
    withDefaultContexts {
        queryBuilder.callback()
    }
    val query = queryBuilder.build()

    return createQuery(translateQuery(cl, query, criteriaBuilder))
}

/**
 * Finds all rows matching the built query.
 */
public inline fun <reified T : Any> EntityManager.findAll(
    noinline callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): List<T> =
    findAll(T::class, callback)

/**
 * Finds all rows matching the built query.
 */
public fun <T : Any> EntityManager.findAll(
    cl: KClass<T>,
    callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): List<T> =
    buildQuery(cl, callback).resultList

/** Finds the first row matching the built query, or null if no rows match */
public inline fun <reified T : Any> EntityManager.findFirstOrNull(
    noinline callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): T? =
    findFirstOrNull(T::class, callback)

/** Finds the first row matching the built query, or null if no rows match */
public fun <T : Any> EntityManager.findFirstOrNull(
    cl: KClass<T>,
    callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): T? =
    buildQuery(cl, callback).setMaxResults(1).resultList.firstOrNull()

/**
 * Finds the row matching the built query.
 *
 * It's an error if there are no matches, or there is more than one match.
 */
public inline fun <reified T : Any> EntityManager.findSingle(
    noinline callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): T =
    findSingle(T::class, callback)

/**
 * Finds the row matching the built query.
 *
 * It's an error if there are no matches, or there is more than one match.
 */
public fun <T : Any> EntityManager.findSingle(
    cl: KClass<T>,
    callback: context(KrExpressionContext, KrSubQueryContext, KrOrderByContext) KrQueryBuilder<T>.() -> Unit
): T =
    buildQuery(cl, callback).singleResult
