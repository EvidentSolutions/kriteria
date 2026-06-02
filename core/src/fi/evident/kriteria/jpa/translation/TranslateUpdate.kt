package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.query.KrUpdate
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaUpdate
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path

internal fun <T : Any> translateUpdate(update: KrUpdate<T>, cb: CriteriaBuilder): CriteriaUpdate<T> {
    val root = update.root

    val result = cb.createCriteriaUpdate(root.rootEntityMeta.entityClass.java)
    val jpaRoot = result.from(root.rootEntityMeta.entityClass.java)

    with(TranslationContext(cb, result, PathCache.singleRoot(root, jpaRoot))) {
        for (binding in update.bindings)
            result.set(binding.translate())

        if (!update.restriction.isAlwaysTrue())
            result.where(update.restriction.translatePredicate())

        return result
    }
}

context(_: TranslationContext)
private fun <T> KrUpdate.Binding<T>.translate(): Pair<Path<T>, Expression<out T>> =
    property.translatePath() to expr.translate()

context(_: TranslationContext)
private fun <T> CriteriaUpdate<*>.set(binding: Pair<Path<T>, Expression<out T>>) {
    set(binding.first, binding.second)
}
