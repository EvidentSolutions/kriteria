package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.KrPredicate
import fi.evident.kriteria.expression.KrPredicate.*
import jakarta.persistence.criteria.Predicate

context(_: TranslationContext)
internal fun KrPredicate.translatePredicate(): Predicate = when (this) {
    False -> cb.disjunction()
    True -> cb.conjunction()
    is And -> cb.and(*predicates.map { it.translatePredicate() }.toTypedArray())
    is Or -> cb.or(*predicates.map { it.translatePredicate() }.toTypedArray())
    is Not -> cb.not(predicate.translate())
    is Like -> cb.like(x.translate(), pattern.translate())
    is NotLike -> cb.notLike(x.translate(), pattern.translate())
    is Between<*> -> cb.between(value.translate(), lower.translate(), upper.translate())
    is IsEmpty -> cb.isEmpty(collection.translateCollection())
    is IsNotEmpty -> cb.isNotEmpty(collection.translateCollection())
    is IsTrue -> cb.isTrue(exp.translate())
    is IsFalse -> cb.isFalse(exp.translate())
    is IsNull -> exp.translate().isNull
    is IsNotNull -> exp.translate().isNotNull
    is IsAnyOf<*> -> value.translate().`in`(collection)
    is ContainsSubquery<*> -> value.translate().`in`(translateSubquery(subQuery))
    is IsEqual<*> -> cb.equal(lhs.translate(), rhs.translate())
    is IsNotEqual<*> -> cb.notEqual(lhs.translate(), rhs.translate())
    is IsLessThan<*> -> cb.lessThan(lhs.translate(), rhs.translate())
    is IsLessThanOrEqualTo<*> -> cb.lessThanOrEqualTo(lhs.translate(), rhs.translate())
    is IsGreaterThan<*> -> cb.greaterThan(lhs.translate(), rhs.translate())
    is IsGreaterThanOrEqualTo<*> -> cb.greaterThanOrEqualTo(lhs.translate(), rhs.translate())
    is Exists -> cb.exists(translateSubquery(subQuery))
    is IsMember<*> -> cb.isMember(element.translate(), collection.translateCollection())
    is IsNotMember<*> -> cb.isNotMember(element.translate(), collection.translateCollection())
}
