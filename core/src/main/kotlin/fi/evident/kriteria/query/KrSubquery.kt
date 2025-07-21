package fi.evident.kriteria.query

import fi.evident.kriteria.expression.KrExpression
import fi.evident.kriteria.expression.KrSubquery
import kotlin.reflect.KClass

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
        groupBy = groupBy,
    )
}
