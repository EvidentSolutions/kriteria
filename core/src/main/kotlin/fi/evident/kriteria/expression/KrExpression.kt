package fi.evident.kriteria.expression

import fi.evident.kriteria.query.KrSubquery
import kotlin.reflect.KClass

/**
 * Base class for all selections in a query.
 *
 * A selection represents a value that can be selected in a query.
 *
 * @param T The type of the selection
 */
public sealed class KrSelection<out T>

/**
 * Base class for all expressions in a query.
 *
 * An expression represents a value that can be used in a query, such as a property path,
 * a literal value, or a function call.
 *
 * @param T The type of the expression
 */
public sealed class KrExpression<out T> : KrSelection<T>()

internal data class LiteralExpression<T>(val value: T) : KrExpression<T>()

internal data class NullLiteralExpression<T : Any>(val type: KClass<T>) : KrExpression<T?>()

internal data class CoerceExpression<T : Any>(val exp: KrExpression<*>, val target: KClass<T>) : KrExpression<T>()

internal sealed class NullExpression<T> : KrExpression<T>() {
    internal data class Coalesce<T>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : NullExpression<T>()
    internal data class NullIf<T>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : NullExpression<T>()
}

internal data class CallExpression<T : Any>(val name: String, val returnType: KClass<T>, val args: List<KrExpression<*>>) : KrExpression<T>()

internal sealed class ComparableExpression<T : Comparable<T>> : KrExpression<T>() {
    internal data class Max<T : Comparable<T>>(val exp: KrExpression<T>) : ComparableExpression<T>()
    internal data class Min<T : Comparable<T>>(val exp: KrExpression<T>) : ComparableExpression<T>()
}

internal sealed class NumericExpression<T : Number> : KrExpression<T>() {
    internal data class UnaryMinus<T : Number>(val exp: KrExpression<T>) : NumericExpression<T>()
    internal data class Plus<T : Number>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : NumericExpression<T>()
    internal data class Minus<T : Number>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : NumericExpression<T>()
    internal data class Multiply<T : Number>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : NumericExpression<T>()
    internal data class Divide<T : Number>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : NumericExpression<T>()
    internal data class Ceiling<T : Number>(val value: KrExpression<T>) : NumericExpression<T>()
    internal data class Floor<T : Number>(val value: KrExpression<T>) : NumericExpression<T>()
    internal data class Modulo(val lhs: KrExpression<Int>, val rhs: KrExpression<Int>) : NumericExpression<Int>()
    internal data class Sqrt(val value: KrExpression<Number>) : NumericExpression<Double>()
    internal data class Exp(val value: KrExpression<Number>) : NumericExpression<Double>()
    internal data class Ln(val value: KrExpression<Number>) : NumericExpression<Double>()
    internal data class Power(val base: KrExpression<Number>, val exponent: KrExpression<Number>) : NumericExpression<Double>()
    internal data class Sign(val value: KrExpression<Number>) : NumericExpression<Int>()
    internal data class Length(val exp: KrExpression<String>) : NumericExpression<Int>()
    internal data class Count(val exp: KrExpression<*>) : NumericExpression<Long>()
    internal data class CountDistinct(val exp: KrExpression<*>) : NumericExpression<Long>()
    internal data class Sum<T : Number>(val exp: KrExpression<T>) : NumericExpression<T>()
    internal data class SumAsLong<T : Number>(val exp: KrExpression<Int>) : NumericExpression<T>()
    internal data class SumAsDouble<T : Number>(val exp: KrExpression<Float>) : NumericExpression<T>()
    internal data class Avg(val exp: KrExpression<Double>) : NumericExpression<Double>()
    internal data class Size(val collection: KrCollectionRef<*>) : NumericExpression<Int>()
}

internal sealed class StringExpression : KrExpression<String>() {
    internal data class Substring(val value: KrExpression<String>, val startIndex: KrExpression<Int>, val endIndex: KrExpression<Int>?) : StringExpression()
    internal data class Uppercase(val value: KrExpression<String>) : StringExpression()
    internal data class Lowercase(val value: KrExpression<String>) : StringExpression()
    internal data class Concat(val lhs: KrExpression<String>, val rhs: KrExpression<String>) : StringExpression()
}

internal class Construct<T : Any>(
    val type: KClass<T>, val args: List<KrSelection<*>>
) : KrSelection<T>()

internal class SelectCase<T>(val whenCases: List<WhenCase<T>>, val otherwise: KrExpression<T>?) : KrExpression<T>()
internal class WhenCase<T>(val condition: KrExpression<Boolean>, val result: KrExpression<T>)

/**
 * Base class for all predicates in a query.
 *
 * A predicate represents a boolean expression that can be used in a WHERE clause
 * or other conditional parts of a query.
 */
public sealed class KrPredicate() : KrExpression<Boolean>() {
    internal open fun isAlwaysTrue() = false
    internal open fun isAlwaysFalse() = false

    internal data object True : KrPredicate() {
        override fun isAlwaysTrue() = true
    }

    internal data object False : KrPredicate() {
        override fun isAlwaysFalse() = true
    }

    internal data class IsEqual<T>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : KrPredicate()
    internal data class IsNotEqual<T>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : KrPredicate()
    internal data class IsLessThan<T : Comparable<T>>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : KrPredicate()
    internal data class IsLessThanOrEqualTo<T : Comparable<T>>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : KrPredicate()
    internal data class IsGreaterThan<T : Comparable<T>>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : KrPredicate()
    internal data class IsGreaterThanOrEqualTo<T : Comparable<T>>(val lhs: KrExpression<T>, val rhs: KrExpression<T>) : KrPredicate()
    internal data class And(val predicates: Collection<KrPredicate>) : KrPredicate()
    internal data class Or(val predicates: Collection<KrPredicate>) : KrPredicate()
    internal data class Not(val predicate: KrExpression<Boolean>) : KrPredicate()
    internal data class Like(val x: KrExpression<String>, val pattern: KrExpression<String>) : KrPredicate()
    internal data class NotLike(val x: KrExpression<String>, val pattern: KrExpression<String>) : KrPredicate()
    internal data class Between<T : Comparable<T>>(val value: KrExpression<T>, val lower: KrExpression<T>, val upper: KrExpression<T>) : KrPredicate()
    internal data class IsEmpty(val collection: KrCollectionRef<*>) : KrPredicate()
    internal data class IsNotEmpty(val collection: KrCollectionRef<*>) : KrPredicate()
    internal data class IsNull(val exp: KrExpression<*>) : KrPredicate()
    internal data class IsNotNull(val exp: KrExpression<*>) : KrPredicate()
    internal data class IsTrue(val exp: KrExpression<Boolean>) : KrPredicate()
    internal data class IsFalse(val exp: KrExpression<Boolean>) : KrPredicate()
    internal data class IsAnyOf<T>(val value: KrExpression<T>, val collection: Collection<T>) : KrPredicate()
    internal data class ContainsSubquery<T : Any>(val value: KrExpression<T>, val subQuery: KrSubquery<T>) : KrPredicate()
    internal data class Exists(val subQuery: KrSubquery<*>) : KrPredicate()
    internal data class IsMember<T>(val element: KrExpression<T>, val collection: KrCollectionRef<T>) : KrPredicate()
    internal data class IsNotMember<T>(val element: KrExpression<T>, val collection: KrCollectionRef<T>) : KrPredicate()
}
