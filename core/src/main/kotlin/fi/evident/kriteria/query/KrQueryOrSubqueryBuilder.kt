package fi.evident.kriteria.query

import fi.evident.kriteria.expression.*
import fi.evident.kriteria.expression.KrJoinType.INNER
import fi.evident.kriteria.expression.KrJoinType.LEFT
import fi.evident.kriteria.jpa.EntityMeta

/**
 * Base class for builders of queries and subqueries.
 */
public abstract class KrQueryOrSubqueryBuilder<S> internal constructor() {
    internal val roots = RootSetBuilder()
    internal var restriction: KrPredicate? = null
    internal var selection: S? = null
    internal var distinct = false

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
    public fun <Y> innerJoin(path: KrManyToOneRef<Y>): KrJoin<*, Y> =
        join(path, INNER, fetch = false)

    /**
     * Creates a left join on the specified many-to-one relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> leftJoin(path: KrManyToOneRef<Y>): KrJoin<*, Y> =
        join(path, LEFT, fetch = false)

    /**
     * Creates a specified join on the given many-to-one relationship.
     */
    context(_: KrExpressionContext)
    internal fun <Y> join(
        path: KrManyToOneRef<Y>,
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
        joinSet(path, INNER)

    /**
     * Creates a left join on the specified collection relationship.
     */
    context(_: KrExpressionContext)
    public fun <Y> leftJoinSet(path: KrCollectionRef<Y>): KrSetJoin<*, Y> =
        joinSet(path, LEFT)

    /**
     * Creates a specified join on the given collection relationship.
     */
    context(_: KrExpressionContext)
    private fun <Y> joinSet(path: KrCollectionRef<Y>, joinType: KrJoinType): KrSetJoin<*, Y> =
        roots.newSetJoin(path.childParent, path.childProperty, joinType)

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
     * Creates a specified join on the given map relationship.
     */
    context(_: KrExpressionContext)
    private fun <K, V> joinMap(
        path: KrMapRef<K, V>,
        joinType: KrJoinType
    ): KrMapJoin<*, K, V> =
        roots.newMapJoin(path.childParent, path.childProperty, joinType)

}
