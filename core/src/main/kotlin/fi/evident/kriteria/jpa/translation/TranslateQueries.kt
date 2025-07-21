package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.KrSelection
import fi.evident.kriteria.expression.KrSubquery
import fi.evident.kriteria.query.KrQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Subquery
import kotlin.reflect.KClass

internal fun <T : Any> translateQuery(
    resultClass: KClass<T>,
    query: KrQuery<KrSelection<T?>>,
    cb: CriteriaBuilder
): CriteriaQuery<T> {
    val result = cb.createQuery(resultClass.java)

    val pathCache = PathCache.build {
        for (root in query.roots.roots)
            addRoot(root, result.from(root.rootEntityMeta.entityClass.java))
    }

    context(TranslationContext(cb, result, pathCache)) {
        for (join in query.roots.joins)
            translateJoin(join)

        result.select(query.selection.translateSelection())
        result.distinct(query.distinct)

        val restriction = query.restriction
        if (restriction != null && !restriction.isAlwaysTrue())
            result.where(restriction.translatePredicate())

        val order = query.order
        if (order != null)
            result.orderBy(order.translateOrder())

        return result
    }
}


context(ctx: TranslationContext)
internal fun <T : Any> translateSubquery(query: KrSubquery<T>): Subquery<T> =
    translateSubquery(ctx, query.resultClass, query)

private fun <T : Any> translateSubquery(
    parentContext: TranslationContext,
    resultClass: KClass<T>,
    query: KrSubquery<T>,
): Subquery<T> {

    val result = parentContext.base.subquery(resultClass.java)
    val subContext = parentContext.createSubContext(result)
    val pathCache = subContext.pathCache

    for (root in query.roots.roots)
        pathCache.addRoot(root, result.from(root.rootEntityMeta.entityClass.java))

    context(subContext) {
        for (join in query.roots.joins)
            translateJoin(join)

        result.select(query.selection.translate())
        result.distinct(query.distinct)

        val restriction = query.restriction
        if (restriction != null && !restriction.isAlwaysTrue())
            result.where(restriction.translatePredicate())

        return result
    }
}
