package fi.evident.kriteria.query

import fi.evident.kriteria.annotations.ExpressionDsl
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.expression.KrJoinType.*
import fi.evident.kriteria.jpa.EntityMeta

/**
 * Base class for builders of queries and subqueries.
 */
@ExpressionDsl
public abstract class KrQueryOrSubqueryBuilder<S> internal constructor() {
    internal val roots = RootSetBuilder()
    internal var restriction: KrPredicate? = null
    internal var selection: S? = null
    internal var distinct = false
    internal var groupBy: List<KrExpression<*>>? = null
    internal var windows = mutableListOf<KrWindow>()

    /**
     * Defines the selection for this query. The results will be distinct.
     *
     * It is an error to call this multiple times.
     */
    public fun selectDistinct(value: S) {
        select(value, distinct = true)
    }

    /**
     * Defines the selection for this query.
     *
     * It is an error to call this multiple times.
     *
     * @param distinct if true, results will be distinct
     */
    public fun select(selection: S, distinct: Boolean = false) {
        check(this.selection == null) { "select already called" }
        this.distinct = distinct
        this.selection = selection
    }

    /**
     * Adds a grouping to this query.
     *
     * It is an error to call this multiple times.
     */
    public fun groupBy(vararg groupBy: KrExpression<*>) {
        require(groupBy.isNotEmpty()) { "empty groupBy not allowed" }
        check(this.groupBy == null) { "groupBy already called" }

        this.groupBy = groupBy.toList()
    }

    /** Creates a new unique root for this query. */
    public fun <T : Any> from(entity: EntityMeta<T, *>): KrRoot<T> =
        roots.newRoot(entity)

    /**
     * Adds a restriction for this query.
     *
     * Only one restriction can be added - subsequent calls will result in an error.
     */
    public fun where(restriction: KrPredicate) {
        check(this.restriction == null) { "where already called" }

        this.restriction = restriction
    }

    /**
     * Adds a conjunction of given restrictions for this query.
     *
     * Only one restriction can be added - subsequent calls will result in an error.
     */
    public fun where(first: KrPredicate, vararg rest: KrPredicate) {
        where(withDefaultContexts { and(first, *rest) })
    }

    /**
     * Creates an inner join on the specified many-to-one relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> innerJoin(path: KrEntityRef<Y>): KrJoin<*, Y> =
        join(path, INNER, fetch = false)

    /**
     * Creates a left join on the specified many-to-one relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> leftJoin(path: KrEntityRef<Y>): KrJoin<*, Y> =
        join(path, LEFT, fetch = false)

    /**
     * Creates a right join on the specified many-to-one relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> rightJoin(path: KrEntityRef<Y>): KrJoin<*, Y> =
        join(path, RIGHT, fetch = false)

    /**
     * Creates a specified join on the given many-to-one relationship.
     */
    context(_: KrExpressionContext)
    internal fun <Y> join(
        path: KrEntityRef<Y>,
        joinType: KrJoinType,
        fetch: Boolean
    ): KrJoin<*, Y> =
        roots.newJoin(path.childParent, path.childProperty, joinType, fetch)

    /**
     * Creates an inner join with an arbitrary condition.
     */
    context(_: KrExpressionContext)
    public fun <E : Any> innerJoin(
        from: KrFrom<*, *>,
        type: EntityMeta<E, *>,
        condition: (KrPath<E>) -> KrPredicate
    ): KrPredicateJoin<*, E> =
        join(from, type, INNER, condition)

    /**
     * Creates a left join with an arbitrary condition.
     */
    context(_: KrExpressionContext)
    public fun <E : Any> leftJoin(
        from: KrFrom<*, *>,
        type: EntityMeta<E, *>,
        condition: (KrPath<E>) -> KrPredicate
    ): KrPredicateJoin<*, E> =
        join(from, type, LEFT, condition)

    /**
     * Creates an inner join on the root with an arbitrary condition.
     */
    context(_: KrExpressionContext)
    private fun <E : Any> join(
        from: KrFrom<*, *>,
        type: EntityMeta<E, *>,
        joinType: KrJoinType,
        predicate: (KrPath<E>) -> KrPredicate
    ): KrPredicateJoin<*, E> =
        roots.newPredicateJoin(from, type, predicate, joinType)

    /**
     * Creates an inner join on the specified collection relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> innerJoinSet(path: KrCollectionRef<Y>): KrSetJoin<*, Y> =
        joinSet(path, INNER, fetch = false)

    /**
     * Creates a left join on the specified collection relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> leftJoinSet(path: KrCollectionRef<Y>): KrSetJoin<*, Y> =
        joinSet(path, LEFT, fetch = false)

    /**
     * Creates a right join on the specified collection relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> rightJoinSet(path: KrCollectionRef<Y>): KrSetJoin<*, Y> =
        joinSet(path, RIGHT, fetch = false)

    /**
     * Creates a specified join on the given collection relationship.
     */
    context(_: KrExpressionContext)
    internal fun <Y> joinSet(path: KrCollectionRef<Y>, joinType: KrJoinType, fetch: Boolean): KrSetJoin<*, Y> =
        roots.newSetJoin(path.childParent, path.childProperty, joinType, fetch = fetch)

    /**
     * Creates an inner join on the specified map relationship.
     */
    context(_: KrExpressionContext)
    public fun <K, V> innerJoinMap(path: KrMapRef<K, V>): KrMapJoin<*, K, V> =
        joinMap(path, INNER)

    /**
     * Creates a left join on the specified map relationship.
     */
    context(_: KrExpressionContext)
    public fun <K, V> leftJoinMap(path: KrMapRef<K, V>): KrMapJoin<*, K, V> =
        joinMap(path, LEFT)

    /**
     * Creates a right join on the specified map relationship.
     */
    context(_: KrExpressionContext)
    public fun <K, V> rightJoinMap(path: KrMapRef<K, V>): KrMapJoin<*, K, V> =
        joinMap(path, RIGHT)

    /**
     * Creates a new query window, taking a block for configuring the window.
     */
    context(_: KrExpressionContext)
    public fun window(init: KrWindowBuilder.() -> Unit): KrWindow {
        val window = KrWindowBuilder().apply(init).build()
        windows.add(window)
        return window
    }

    /**
     * Creates a specified join on the given map relationship.
     */
    context(_: KrExpressionContext)
    private fun <K, V> joinMap(
        path: KrMapRef<K, V>,
        joinType: KrJoinType
    ): KrMapJoin<*, K, V> =
        roots.newMapJoin(path.childParent, path.childProperty, joinType)

}
