package fi.evident.kriteria.query

import fi.evident.kriteria.expression.KrExpression
import fi.evident.kriteria.expression.KrPredicate
import kotlin.reflect.KClass

/**
 * Represents a subquery.
 */
public class KrSubquery<T : Any> internal constructor(
    internal val resultClass: KClass<T>,
    internal val roots: RootSet,
    internal val selection: KrExpression<T>,
    internal val distinct: Boolean,
    internal val restriction: KrPredicate?,
)

/**
 * Context for building subqueries.
 */
public class KrSubqueryBuilder<T : Any> internal constructor(
    private val resultClass: KClass<T>,
) : KrQueryOrSubqueryBuilder<KrExpression<T>>() {

    internal fun build(): KrSubquery<T> = KrSubquery(
        resultClass = resultClass,
        roots = roots.build(),
        selection = selection ?: error("select(...) was not called for sub-query"),
        distinct = distinct,
        restriction = restriction,
    )
}
