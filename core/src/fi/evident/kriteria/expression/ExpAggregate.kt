package fi.evident.kriteria.expression

/** Creates an aggregate expression that applies the count operation */
context(_: KrExpressionContext)
public fun count(): KrExpression<Long> =
    count(literal(1))

/** Creates an aggregate expression that applies the count operation */
context(_: KrExpressionContext)
public fun count(x: KrExpression<*>): KrExpression<Long> =
    NumericExpression.Count(x)

/** Creates an aggregate expression that applies the count distinct operation */
context(_: KrExpressionContext)
public fun countDistinct(x: KrExpression<*>): KrExpression<Long> =
    NumericExpression.CountDistinct(x)

/** Creates an aggregate expression that calculates the maximum value of a given expression */
context(_: KrExpressionContext)
public fun <T : Comparable<T>> max(x: KrExpression<T>): KrExpression<T> =
    ComparableExpression.Max(x)

/** Creates an aggregate expression that calculates the minimum value of a given expression */
context(_: KrExpressionContext)
public fun <T : Comparable<T>> min(x: KrExpression<T>): KrExpression<T> =
    ComparableExpression.Min(x)

/** Creates an aggregate expression that calculates the average value of a given expression */
context(_: KrExpressionContext)
public fun avg(x: KrExpression<Double>): KrExpression<Double> =
    NumericExpression.Avg(x)

/** Creates an aggregate expression that calculates the sum of a given expression */
context(_: KrExpressionContext)
public fun <T : Number> sum(x: KrExpression<T>): KrExpression<T> =
    NumericExpression.Sum(x)

/** Creates an aggregate expression that calculates the sum of a given expression */
context(_: KrExpressionContext)
public fun sumAsLong(x: KrExpression<Int>): KrExpression<Long> =
    NumericExpression.SumAsLong(x)

/** Creates an aggregate expression that calculates the sum of a given expression */
context(_: KrExpressionContext)
public fun sumAsDouble(x: KrExpression<Float>): KrExpression<Double> =
    NumericExpression.SumAsDouble(x)

/** Counts elements matching the given KriteriaPredicate */
context(_: KrExpressionContext)
public fun countMatches(predicate: KrPredicate): KrExpression<Long> =
    sumAsLong(ifThenElse(predicate, 1, 0))
