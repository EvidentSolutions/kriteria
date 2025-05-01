package fi.evident.kriteria.jpa.translation

import jakarta.persistence.criteria.CommonAbstractCriteria
import jakarta.persistence.criteria.CriteriaBuilder

/** A context for translating criteria expressions to Hibernate. */
internal class TranslationContext(
    val cb: CriteriaBuilder,
    val base: CommonAbstractCriteria,
    val pathCache: PathCache,
) {

    /** Create a new context for a sub-query of the original query. */
    fun createSubContext(base: CommonAbstractCriteria) = TranslationContext(cb, base, pathCache)
}

context(ctx: TranslationContext)
internal val cb: CriteriaBuilder
    get() = ctx.cb
