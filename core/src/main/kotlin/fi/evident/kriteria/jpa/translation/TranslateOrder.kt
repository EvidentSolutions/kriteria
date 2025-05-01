package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.KrOrder
import jakarta.persistence.criteria.Order

context(_: TranslationContext)
internal fun KrOrder.translateOrder(): List<Order> =
    toList().map { it.translateOrder() }

context(_: TranslationContext)
internal fun KrOrder.Simple.translateOrder(): Order = when (this) {
    is KrOrder.Asc -> cb.asc(exp.translate())
    is KrOrder.Desc -> cb.desc(exp.translate())
}
