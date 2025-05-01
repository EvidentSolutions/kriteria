package fi.evident.kriteria.expression

/** Checks if a given expression is null */
context(_: KrExpressionContext)
public fun isNull(predicate: KrExpression<*>): KrPredicate =
    KrPredicate.IsNull(predicate)

/** Checks if a given expression is not null */
context(_: KrExpressionContext)
public fun isNotNull(predicate: KrExpression<*>): KrPredicate =
    KrPredicate.IsNotNull(predicate)

/** Returns the first non-null value of the given expressions */
context(_: KrExpressionContext)
public fun <T> coalesce(x: KrExpression<T>, y: KrExpression<T>): KrExpression<T> =
    NullExpression.Coalesce(x, y)

/** Returns the first non-null value of the given expressions */
context(_: KrExpressionContext)
public fun <T> coalesce(x: KrExpression<T>, y: T): KrExpression<T> =
    coalesce(x, literal(y))

/** Returns the first non-null value of the given expressions */
context(_: KrExpressionContext)
public fun <T> nullIf(x: KrExpression<T>, y: KrExpression<T>): KrExpression<T> =
    NullExpression.NullIf(x, y)

/** Returns the first non-null value of the given expressions */
context(_: KrExpressionContext)
public fun <T> nullIf(x: KrExpression<T>, y: T): KrExpression<T> =
    nullIf(x, literal(y))
