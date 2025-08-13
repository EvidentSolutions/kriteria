package fi.evident.kriteria.expression

import fi.evident.kriteria.annotations.DelicateCriteriaApi
import fi.evident.kriteria.jpa.translation.TranslationContext
import fi.evident.kriteria.jpa.translation.translate
import jakarta.persistence.criteria.Expression
import org.hibernate.query.criteria.HibernateCriteriaBuilder

/**
 * Uses Hibernate APIs to translate expression to native Hibernate expression.
 */
@DelicateCriteriaApi
context(_: KrExpressionContext)
public fun <T> hibernate(callback: HibernateExpressionContext.() -> Expression<T>): KrExpression<T> =
    HibernateExpression(callback)

public class HibernateExpressionContext internal constructor(
    public val cb: HibernateCriteriaBuilder,
    private val translationContext: TranslationContext,
) {

    public fun <T> translate(expr: KrExpression<T>): Expression<T> =
        context(translationContext) {
            expr.translate()
        }
}
