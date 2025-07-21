package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.*
import fi.evident.kriteria.expression.ComparableExpression.Max
import fi.evident.kriteria.expression.ComparableExpression.Min
import fi.evident.kriteria.expression.NullExpression.Coalesce
import fi.evident.kriteria.expression.NullExpression.NullIf
import fi.evident.kriteria.expression.NumericExpression.*
import fi.evident.kriteria.expression.StringExpression.*
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import org.hibernate.query.criteria.JpaExpression

context(_: TranslationContext)
internal fun <T> KrExpression<T>.translate(): Expression<T> {
    val result: Expression<*> = when (this) {
        is KrPredicate -> translatePredicate()
        is KrPath -> translatePath()
        is NumericExpression -> translateNumeric()
        is StringExpression -> translateString()
        is ComparableExpression<*> -> translateComparable()
        is CoerceExpression<*> -> translateCoercion()
        is NullExpression -> translateNullExpression()
        is SelectCase -> translateSelectCase()
        is KrSubquery<*> -> translateSubquery(this)
        is LiteralExpression -> cb.literal(value)
        is NullLiteralExpression<*> -> cb.nullLiteral(type.java)
        is CallExpression -> cb.function(name, returnType.java, *args.map { it.translate() }.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    return result as Expression<T>
}

context(_: TranslationContext)
private fun <T : Comparable<T>> ComparableExpression<T>.translateComparable(): Expression<T> = when (this) {
    is Max -> cb.greatest(exp.translate())
    is Min -> cb.least(exp.translate())
}

context(_: TranslationContext)
private fun <T> NullExpression<T>.translateNullExpression(): Expression<T> = when (this) {
    is NullIf -> cb.nullIf(lhs.translate(), rhs.translate())
    is Coalesce -> cb.coalesce(lhs.translate(), rhs.translate())
}

// extracted to solve typing issues
private fun <T> CriteriaBuilder.nullIf(lhs: Expression<T>, rhs: Expression<*>): Expression<T> =
    nullif(lhs, rhs)

context(_: TranslationContext)
private fun StringExpression.translateString(): Expression<String> = when (this) {
    is Substring -> translateSubstring(value, startIndex, endIndex)
    is Uppercase -> cb.upper(value.translate())
    is Lowercase -> cb.lower(value.translate())
    is Concat -> cb.concat(lhs.translate(), rhs.translate())
}

context(_: TranslationContext)
private fun NumericExpression<*>.translateNumeric(): Expression<out Number> = when (this) {
    is UnaryMinus -> cb.neg(exp.translate())
    is Plus -> cb.sum(lhs.translate(), rhs.translate())
    is Minus -> cb.diff(lhs.translate(), rhs.translate())
    is Multiply -> cb.prod(lhs.translate(), rhs.translate())
    is Divide -> cb.quot(lhs.translate(), rhs.translate())
    is Ceiling -> cb.ceiling(value.translate())
    is Floor -> cb.floor(value.translate())
    is Modulo -> cb.mod(lhs.translate(), rhs.translate())
    is Sqrt -> cb.sqrt(value.translate())
    is Exp -> cb.exp(value.translate())
    is Ln -> cb.ln(value.translate())
    is Power -> cb.power(base.translate(), exponent.translate())
    is Sign -> cb.sign(value.translate())
    is Count -> cb.count(exp.translate())
    is CountDistinct -> cb.countDistinct(exp.translate())
    is Sum -> cb.sum(exp.translate())
    is SumAsLong -> cb.sumAsLong(exp.translate())
    is SumAsDouble -> cb.sumAsDouble(exp.translate())
    is Avg -> cb.avg(exp.translate())
    is Length -> cb.length(exp.translate())
    is Size -> cb.size(collection.translateCollection())
}

context(_: TranslationContext)
private fun <T : Any> CoerceExpression<T>.translateCoercion(): Expression<T> {
    val translated = exp.translate()
    if (translated !is JpaExpression)
        throw UnsupportedOperationException("Coercion is only supported for Hibernate")

    return translated.cast(target.java)
}

context(_: TranslationContext)
private fun translateSubstring(
    value: KrExpression<String>,
    startIndex: KrExpression<Int>,
    endIndex: KrExpression<Int>?,
): Expression<String> = withDefaultContexts {

    // Convert Kotlin's 0-based startIndex and endIndex to 1-based start and length
    val jpaValue = value.translate()
    val jpaStart = (startIndex + 1).translate()
    val jpaLength = endIndex?.minus(startIndex)?.translate()

    return if (jpaLength == null) cb.substring(jpaValue, jpaStart) else cb.substring(jpaValue, jpaStart, jpaLength)
}

context(_: TranslationContext)
private fun <T> SelectCase<T>.translateSelectCase(): Expression<T> {
    val result = cb.selectCase<T>()

    for (whenCase in whenCases)
        result.`when`(whenCase.condition.translate(), whenCase.result.translate())

    otherwise?.let {
        result.otherwise(it.translate())
    }

    return result
}
