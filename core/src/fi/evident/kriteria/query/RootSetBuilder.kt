package fi.evident.kriteria.query

import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.EntityMeta

internal class RootSet(
    val roots: List<KrRoot<*>>,
    val joins: List<KrAnyJoin<*, *>>,
)

internal class RootSetBuilder {
    private val roots = mutableListOf<KrRoot<*>>()
    private val joins = mutableListOf<KrAnyJoin<*, *>>()

    fun <T : Any> newRoot(entity: EntityMeta<T, *>): KrRoot<T> {
        val root = KrRoot(entity)
        roots += root
        return root
    }

    fun <X, Y> newJoin(
        parent: KrFrom<*, out X>,
        name: String,
        joinType: KrJoinType,
        fetch: Boolean
    ): KrJoin<X, Y> {
        val join = KrJoin<X, Y>(parent, name, joinType, fetch)
        joins += join
        return join
    }

    fun <X, Y> newSetJoin(
        parent: KrFrom<*, out X>,
        name: String,
        joinType: KrJoinType,
        fetch: Boolean,
    ): KrSetJoin<*, Y> {
        val join = KrSetJoin<X, Y>(parent, name, joinType, fetch)
        joins += join
        return join
    }

    fun <X, K, V> newMapJoin(
        parent: KrFrom<*, out X>,
        name: String,
        joinType: KrJoinType
    ): KrMapJoin<*, K, V> {
        val join = KrMapJoin<X, K, V>(parent, name, joinType)
        joins += join
        return join
    }

    fun <E : Any> newPredicateJoin(
        from: KrFrom<*, *>,
        type: EntityMeta<E, *>,
        predicate: (KrPath<E>) -> KrPredicate,
        joinType: KrJoinType
    ): KrPredicateJoin<*, E> {
        val join = KrPredicateJoin(from, type, predicate, joinType)
        joins += join
        return join
    }

    fun build() = RootSet(roots, joins)
}
