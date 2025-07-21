package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.*
import fi.evident.kriteria.expression.KrJoinType.*
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import org.hibernate.query.criteria.JpaFrom
import org.hibernate.query.sqm.tree.SqmJoinType

context(ctx: TranslationContext)
internal fun translateJoin(join: KrAnyJoin<*, *>) {
    context(ctx.pathCache) {
        when (join) {
            is KrJoin<*, *> -> translatePlainJoin(join)
            is KrPredicateJoin<*, *> -> translatePredicateJoin(join)
            is KrSetJoin<*, *> -> translateSetJoin(join)
            is KrMapJoin<*, *, *> -> translateMapJoin(join)
        }
    }
}

context(pathCache: PathCache)
private fun <X, Y> translatePlainJoin(join: KrJoin<X, Y>) {
    val from = pathCache.resolveFrom(join.joinParent)
    val type = join.joinType.translate()

    val result = if (join.joinFetch) {
        // Treat a JpaFetch as a JpaJoin. This is somewhat dubious but works in Hibernate.
        @Suppress("UNCHECKED_CAST")
        from.fetch<X, Y>(join.joinProperty, type) as Join<X, Y>
    } else {
        from.join(join.joinProperty, type)
    }

    pathCache.addJoin(join, result)
}

context(ctx: TranslationContext, pathCache: PathCache)
private fun <X, Y : Any> translatePredicateJoin(join: KrPredicateJoin<X, Y>) {
    val from = pathCache.resolveFrom(join.joinParent)
    if (from !is JpaFrom<*, *>)
        throw UnsupportedOperationException("Predicate join is only supported for Hibernate")

    val type = SqmJoinType.from(join.joinType.translate())

    val j = from.join(join.joinEntity.entityClass.java, type)

    // We need to add the generated join to the cache already at this point,
    // since the predicate refers to it
    pathCache.addJoin(join, j)
    j.on(join.joinPredicate(join).translatePredicate())
}

context(pathCache: PathCache)
private fun <X, Y> translateSetJoin(join: KrSetJoin<X, Y>) {
    val parent = pathCache.resolveFrom(join.joinParent)
    val result = parent.joinSet<X, Y>(join.joinProperty, join.joinType.translate())
    pathCache.addJoin(join, result)
}

context(pathCache: PathCache)
private fun <X, K, V> translateMapJoin(join: KrMapJoin<X, K, V>) {
    val parent = pathCache.resolveFrom(join.joinParent)
    val result =  parent.joinMap<X, K, V>(join.joinProperty, join.joinType.translate())
    pathCache.addJoin(join, result)
}

private fun KrJoinType.translate(): JoinType = when (this) {
    INNER -> JoinType.INNER
    LEFT -> JoinType.LEFT
    RIGHT -> JoinType.RIGHT
}
