package fi.evident.kriteria.expression

/** Creates an inclusive range from this expression to the given end expression (both bounds included) */
context(_: KrExpressionContext)
public operator fun <T : Comparable<T>> KrExpression<T>.rangeTo(end: KrExpression<T>): InclusiveExpressionRange<T> =
    InclusiveExpressionRange(this, end)

/** Creates an exclusive range from this expression up to but not including the given end expression */
context(_: KrExpressionContext)
public operator fun <T : Comparable<T>> KrExpression<T>.rangeUntil(end: KrExpression<T>): ExclusiveExpressionRange<T> =
    ExclusiveExpressionRange(this, end)

/** Represents a range with inclusive start and end bounds */
public class InclusiveExpressionRange<T : Comparable<T>> internal constructor(
    internal val start: KrExpression<T>,
    internal val endInclusive: KrExpression<T>,
)

/** Represents a range with inclusive start and exclusive end bound */
public class ExclusiveExpressionRange<T : Comparable<T>> internal constructor(
    internal val start: KrExpression<T>,
    internal val endExclusive: KrExpression<T>,
)

/** Checks if this value is within the given inclusive range */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> T.isInRange(range: InclusiveExpressionRange<T>): KrPredicate =
    literal(this) isInRange range

/** Checks if this value is within the given exclusive range */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> T.isInRange(range: ExclusiveExpressionRange<T>): KrPredicate =
    literal(this) isInRange range

/** Checks if this expression evaluates to a value within the given inclusive range */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isInRange(range: InclusiveExpressionRange<T>): KrPredicate =
    KrPredicate.Between(this, range.start, range.endInclusive)

/** Checks if this expression evaluates to a value within the given exclusive range */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isInRange(range: ExclusiveExpressionRange<T>): KrPredicate =
    and(this isGreaterThanOrEqualTo range.start, this isLessThan range.endExclusive)

/** Checks if this expression evaluates to a value within the given closed range */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.between(range: ClosedRange<T>): KrPredicate =
    KrPredicate.Between(this, literal(range.start), literal(range.endInclusive))

/** Check whether given value is within given open-ended range */
context(_: KrExpressionContext)
public infix fun <T : Comparable<T>> KrExpression<T>.isInRange(range: OpenEndRange<T>): KrPredicate =
    and(this isGreaterThanOrEqualTo range.start, this isLessThan range.endExclusive)
