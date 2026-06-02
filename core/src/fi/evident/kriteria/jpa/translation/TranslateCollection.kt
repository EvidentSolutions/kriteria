package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.KrCollectionRef
import jakarta.persistence.criteria.Expression

context(ctx: TranslationContext)
internal fun <T> KrCollectionRef<T>.translateCollection(): Expression<Collection<T>> =
    childParent.translatePath().get(childProperty)
