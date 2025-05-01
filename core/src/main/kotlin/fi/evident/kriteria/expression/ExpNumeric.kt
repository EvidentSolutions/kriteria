package fi.evident.kriteria.expression

/** Negates the arithmetic expression */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.unaryMinus(): KrExpression<T> =
    NumericExpression.UnaryMinus(this)

/** Computes the sum of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.plus(y: KrExpression<T>): KrExpression<T> =
    handleConstantIntBinOp(this, y, Int::plus) ?: NumericExpression.Plus(this, y)

/** Computes the sum of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.plus(y: T): KrExpression<T> =
    this + literal(y)

/** Computes the sum of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> T.plus(y: KrExpression<T>): KrExpression<T> =
    literal(this) + y

/** Computes the difference of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.minus(y: KrExpression<T>): KrExpression<T> =
    handleConstantIntBinOp(this, y, Int::minus) ?: NumericExpression.Minus(this, y)

/** Computes the difference of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.minus(y: T): KrExpression<T> =
    this - literal(y)

/** Computes the difference of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> T.minus(y: KrExpression<T>): KrExpression<T> =
    literal(this) - y

/** Computes the product of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.times(y: KrExpression<T>): KrExpression<T> =
    handleConstantIntBinOp(this, y, Int::times) ?: NumericExpression.Multiply(this, y)

/** Computes the product of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.times(y: T): KrExpression<T> =
    this * literal(y)

/** Computes the product of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> T.times(y: KrExpression<T>): KrExpression<T> =
    literal(this) * y

/** Computes the quotient of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.div(y: KrExpression<T>): KrExpression<T> =
    NumericExpression.Divide(this, y)

/** Computes the quotient of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> KrExpression<T>.div(y: T): KrExpression<T> =
    this / literal(y)

/** Computes the quotient of two numeric expressions */
context(_: KrExpressionContext)
public operator fun <T : Number> T.div(y: KrExpression<T>): KrExpression<T> =
    literal(this) / y

/** Computes the quotient of two numeric expressions */
context(_: KrExpressionContext)
public operator fun KrExpression<Int>.rem(y: KrExpression<Int>): KrExpression<Int> =
    NumericExpression.Modulo(this, y)

/** Computes the quotient of two numeric expressions */
context(_: KrExpressionContext)
public operator fun KrExpression<Int>.rem(y: Int): KrExpression<Int> =
    this % literal(y)

/** Computes the quotient of two numeric expressions */
context(_: KrExpressionContext)
public operator fun Int.rem(y: KrExpression<Int>): KrExpression<Int> =
    literal(this) % y

/** Computes the square root of the value */
context(_: KrExpressionContext)
public fun sqrt(x: KrExpression<Number>): KrExpression<Double> =
    NumericExpression.Sqrt(x)

/** Computes the sign of the value */
context(_: KrExpressionContext)
public fun sign(x: KrExpression<Number>): KrExpression<Int> =
    NumericExpression.Sign(x)

/** Computes the ceiling of the value */
context(_: KrExpressionContext)
public fun <T : Number> ceil(x: KrExpression<T>): KrExpression<T> =
    NumericExpression.Ceiling(x)

/** Computes the floor of the value */
context(_: KrExpressionContext)
public fun <T : Number> floor(x: KrExpression<T>): KrExpression<T> =
    NumericExpression.Floor(x)

/** Returns the exponential of the given value */
context(_: KrExpressionContext)
public fun exp(x: KrExpression<Number>): KrExpression<Double> =
    NumericExpression.Exp(x)

/** Returns the natural logarithm of the given value */
context(_: KrExpressionContext)
public fun ln(x: KrExpression<Number>): KrExpression<Double> =
    NumericExpression.Ln(x)

/** Calculates base to power of exponent */
context(_: KrExpressionContext)
public fun KrExpression<Number>.pow(exponent: KrExpression<Number>): KrExpression<Double> =
    NumericExpression.Power(this, exponent)

/** Calculates base to power of exponent */
context(_: KrExpressionContext)
public fun KrExpression<Number>.pow(exponent: Number): KrExpression<Double> =
    pow(literal(exponent))

/** Calculates base to power of exponent */
context(_: KrExpressionContext)
public fun Number.pow(exponent: KrExpression<Number>): KrExpression<Double> =
    literal(this).pow(exponent)


@Suppress("UNCHECKED_CAST")
context(_: KrExpressionContext)
private inline fun <T : Number> handleConstantIntBinOp(lhs: KrExpression<T>, rhs: KrExpression<T>, op: (Int, Int) -> Int): KrExpression<T>? {
    if (lhs is LiteralExpression && rhs is LiteralExpression) {
        val lhsValue = lhs.value
        val rhsValue = rhs.value
        if (lhsValue is Int && rhsValue is Int)
            return literal(op(lhsValue, rhsValue)) as KrExpression<T>
    }
    return null
}
