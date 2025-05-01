package fi.evident.kriteria.query

import fi.evident.kriteria.expression.*

internal class KrQuery<T> internal constructor(
    internal val roots: RootSet,
    internal val selection: T,
    internal val distinct: Boolean,
    internal val restriction: KrPredicate?,
    internal val order: KrOrder?,
)

/**
 * Context for building queries.
 */
public class KrQueryBuilder<T> internal constructor() : KrQueryOrSubqueryBuilder<KrSelection<T?>>() {

    private var order: KrOrder? = null

    /**
     * Create a fetch join to the specified attribute using an inner join.
     */
    context(_: KrExpressionContext)
    public fun <Y> fetch(path: KrManyToOneRef<Y>): KrJoin<*, Y> =
        join(path, KrJoinType.INNER, true)

    /**
     * Create a fetch join to the specified attribute using a left join.
     */
    context(_: KrExpressionContext)
    public fun <Y> fetchOptional(path: KrManyToOneRef<Y>): KrJoin<*, Y> =
        join(path, KrJoinType.LEFT, fetch = true)

    /**
     * Sets the ordering for this query.
     *
     * It is an error to call this multiple times.
     */
    public fun orderBy(order: KrOrder) {
        check(this.order == null) { "orderBy already called" }
        this.order = order
    }

    internal fun build(): KrQuery<KrSelection<T?>> = KrQuery(
        roots = roots.build(),
        selection = this.selection ?: error("select must be called"),
        distinct = distinct,
        restriction = restriction,
        order = order,
    )
}
