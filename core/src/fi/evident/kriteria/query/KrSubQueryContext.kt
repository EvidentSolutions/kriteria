package fi.evident.kriteria.query

import fi.evident.kriteria.annotations.ExpressionDsl
import fi.evident.kriteria.expression.KrPredicate
import fi.evident.kriteria.expression.KrRoot
import fi.evident.kriteria.expression.KrSubquery
import fi.evident.kriteria.expression.LiteralExpression
import fi.evident.kriteria.jpa.EntityMeta
import kotlin.reflect.KClass

/**
 * Context for cases where subqueries are supported.
 */
@ExpressionDsl
public abstract class KrSubQueryContext internal constructor()

/**
 * Creates a sub-query that returns elements of type [T].
 */
context(_: KrSubQueryContext)
public inline fun <reified T : Any> subquery(
    noinline callback: KrSubqueryBuilder<T>.() -> Unit
): KrSubquery<T> =
    subquery(T::class) { callback() }

/**
 * Creates a sub-query that returns elements of type [T].
 */
context(_: KrSubQueryContext)
public fun <T : Any> subquery(
    resultType: KClass<T>,
    callback: KrSubqueryBuilder<T>.() -> Unit
): KrSubquery<T> {
    val builder = KrSubqueryBuilder(resultType)
    builder.callback()
    return builder.build()
}

/**
 * Creates a predicate that checks if a given subquery returns at least one result.
 */
context(_: KrSubQueryContext)
public fun <E : Any> exists(
    entityMeta: EntityMeta<E, *>,
    callback: KrSubqueryBuilder<Int>.(KrRoot<E>) -> KrPredicate
): KrPredicate =
    KrPredicate.Exists(subquery(Int::class) {
        val value = from(entityMeta)
        select(LiteralExpression(1))
        where(callback(value))
    })
