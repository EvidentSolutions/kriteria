package fi.evident.kriteria.expression

/** Checks whether two expressions are equal */
context(_: KrExpressionContext)
public infix fun <T> KrExpression<T>.isEqualTo(y: KrExpression<T>): KrPredicate =
    KrPredicate.IsEqual(this, y)

/** Checks whether an expression is equal to the given value */
context(_: KrExpressionContext)
public infix fun <T> KrExpression<T>.isEqualTo(y: T?): KrPredicate =
    this isEqualTo literal(y)

/** Checks whether two expressions are not equal */
context(_: KrExpressionContext)
public infix fun <T> KrExpression<T>.isNotEqualTo(y: KrExpression<T>): KrPredicate =
    KrPredicate.IsNotEqual(this, y)

/** Checks whether an expression is not equal to the given value */
context(_: KrExpressionContext)
public infix fun <T> KrExpression<T>.isNotEqualTo(y: T?): KrPredicate =
    this isNotEqualTo literal(y)

/** Checks if the first value is less than the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isLessThan(y: KrExpression<T>): KrPredicate =
    KrPredicate.IsLessThan(this, y)

/** Checks if the first value is less than the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isLessThan(y: T): KrPredicate =
    this isLessThan literal(y)

/** Checks if the first value is less than or equal to the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isLessThanOrEqualTo(y: KrExpression<T>): KrPredicate =
    KrPredicate.IsLessThanOrEqualTo(this, y)

/** Checks if the first value is less than or equal to the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isLessThanOrEqualTo(y: T): KrPredicate =
    this isLessThanOrEqualTo literal(y)

/** Checks if the first value is greater than the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isGreaterThan(y: KrExpression<T>): KrPredicate =
    KrPredicate.IsGreaterThan(this, y)

/** Checks if the first value is greater than the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isGreaterThan(y: T): KrPredicate =
    this isGreaterThan literal(y)

/** Checks if the first value is greater than or equal to the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isGreaterThanOrEqualTo(y: KrExpression<T>): KrPredicate =
    KrPredicate.IsGreaterThanOrEqualTo(this, y)

/** Checks if the first value is greater than or equal to the second */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isGreaterThanOrEqualTo(y: T): KrPredicate =
    this isGreaterThanOrEqualTo literal(y)
