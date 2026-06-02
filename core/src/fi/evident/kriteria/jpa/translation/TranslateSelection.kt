package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.Construct
import fi.evident.kriteria.expression.KrExpression
import fi.evident.kriteria.expression.KrSelection
import jakarta.persistence.criteria.Selection

context(_: TranslationContext)
internal fun <T> KrSelection<T>.translateSelection(): Selection<out T> = when (this) {
    is Construct -> cb.construct(type.java, *args.map { it.translateSelection() }.toTypedArray())
    is KrExpression -> translate()
}
