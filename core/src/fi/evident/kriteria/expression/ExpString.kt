package fi.evident.kriteria.expression

/** Checks if an expression matches the given pattern */
context(_: KrExpressionContext)
public infix fun KrExpression<String>.like(pattern: String): KrPredicate =
    KrPredicate.Like(this, literal(pattern))

/** Checks if an expression does not match the given pattern */
context(_: KrExpressionContext)
public infix fun KrExpression<String>.notLike(pattern: String): KrPredicate =
    KrPredicate.NotLike(this, literal(pattern))

/** Concatenates given strings */
context(_: KrExpressionContext)
public fun concat(lhs: KrExpression<String>, rhs: KrExpression<String>): KrExpression<String> =
    StringExpression.Concat(lhs, rhs)

/** Concatenates given strings */
context(_: KrExpressionContext)
public fun concat(lhs: String, rhs: KrExpression<String>): KrExpression<String> =
    concat(literal(lhs), rhs)

/** Concatenates given strings */
context(_: KrExpressionContext)
public fun concat(lhs: KrExpression<String>, rhs: String): KrExpression<String> =
    concat(lhs, literal(rhs))

/** Returns the length of the given string */
context(_: KrExpressionContext)
public val KrExpression<String>.length: KrExpression<Int>
    get() = NumericExpression.Length(this)

/** Converts the string to uppercase */
context(_: KrExpressionContext)
public fun KrExpression<String>.toUppercase(): KrExpression<String> =
    StringExpression.Uppercase(this)

/** Converts the string to lowercase */
context(_: KrExpressionContext)
public fun KrExpression<String>.toLowercase(): KrExpression<String> =
    StringExpression.Lowercase(this)

/**
 * Returns the substring from the given index to the end of the string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(from: KrExpression<Int>): KrExpression<String> =
    StringExpression.Substring(this, from, null)

/**
 * Returns the substring from the given index to the end of the string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(from: Int): KrExpression<String> =
    substring(literal(from))

/**
 * Returns the substring between given indices of a string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(startIndex: KrExpression<Int>, endIndex: KrExpression<Int>): KrExpression<String> =
    StringExpression.Substring(this, startIndex, endIndex)

/**
 * Returns the substring between given indices of a string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(startIndex: KrExpression<Int>, endIndex: Int): KrExpression<String> =
    substring(startIndex, literal(endIndex))

/**
 * Returns the substring between given indices of a string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(startIndex: Int, endIndex: KrExpression<Int>): KrExpression<String> =
    substring(literal(startIndex), endIndex)

/**
 * Returns the substring between given indices of a string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(startIndex: Int, endIndex: Int): KrExpression<String> =
    substring(literal(startIndex), literal(endIndex))

/**
 * Returns the substring between given indices of a string.
 *
 * Note that the API mirrors that of [String.substring] and indices start at 0, not 1.
 */
context(_: KrExpressionContext)
public fun KrExpression<String>.substring(range: OpenEndRange<Int>): KrExpression<String> =
    substring(range.start, range.endExclusive)

/** Checks if a string starts with the specified prefix */
context(_: KrExpressionContext)
public infix fun KrExpression<String>.startsWith(prefix: String): KrPredicate =
    this like "$prefix%"

/** Checks if a string ends with the specified suffix */
context(_: KrExpressionContext)
public infix fun KrExpression<String>.endsWith(suffix: String): KrPredicate =
    this like "%$suffix"

/** Checks if a string contains the specified substring */
context(_: KrExpressionContext)
public infix fun KrExpression<String>.contains(substr: String): KrPredicate =
    this like "%$substr%"

/** Checks if a string does not contain the specified substring */
context(_: KrExpressionContext)
public infix fun KrExpression<String>.doesNotContain(substr: String): KrPredicate =
    this notLike "%$substr%"
