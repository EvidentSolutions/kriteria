package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.KrPredicate
import fi.evident.kriteria.expression.KrRoot
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaDelete

internal fun <T : Any> translateDelete(
    root: KrRoot<T>,
    restriction: KrPredicate,
    cb: CriteriaBuilder
): CriteriaDelete<T> {
    val result = cb.createCriteriaDelete(root.rootEntityMeta.entityClass.java)
    val jpaRoot = result.from(root.rootEntityMeta.entityClass.java)

    context(TranslationContext(cb, result, PathCache.singleRoot(root, jpaRoot))) {
        if (!restriction.isAlwaysTrue())
            result.where(restriction.translatePredicate())
        return result
    }
}
