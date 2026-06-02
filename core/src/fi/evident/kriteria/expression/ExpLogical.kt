package fi.evident.kriteria.expression

/** Returns a Predicate that is always true */
context(_: KrExpressionContext)
public fun alwaysTrue(): KrPredicate =
    KrPredicate.True

/** Returns a Predicate that is always false */
context(_: KrExpressionContext)
public fun alwaysFalse(): KrPredicate =
    KrPredicate.False

/** Checks if all the given predicates are true */
context(_: KrExpressionContext)
public fun and(vararg predicates: KrPredicate): KrPredicate =
    and(predicates.asList())

/** Checks if all the given predicates are true */
context(_: KrExpressionContext)
public fun and(predicates: Collection<KrPredicate>): KrPredicate {
    if (predicates.any { it.isAlwaysFalse() })
        return KrPredicate.False

    val normalized = predicates.filterNot { it.isAlwaysTrue() }
    return when {
        normalized.isEmpty() -> KrPredicate.True
        normalized.size == 1 -> normalized.first()
        else -> KrPredicate.And(normalized)
    }
}

/** Checks if any of the given predicates are true */
context(_: KrExpressionContext)
public fun or(vararg predicates: KrPredicate): KrPredicate =
    or(predicates.asList())

/** Checks if any of the given predicates are true */
context(_: KrExpressionContext)
public fun or(predicates: Collection<KrPredicate>): KrPredicate {
    if (predicates.any { it.isAlwaysTrue() })
        return KrPredicate.True

    val normalized = predicates.filterNot { it.isAlwaysFalse() }
    return when {
        normalized.isEmpty() -> KrPredicate.False
        normalized.size == 1 -> normalized.first()
        else -> KrPredicate.Or(normalized)
    }
}

/** Checks if a given predicate is not true  */
context(_: KrExpressionContext)
public fun not(exp: KrPredicate): KrPredicate = when {
    exp.isAlwaysTrue() -> KrPredicate.False
    exp.isAlwaysFalse() -> KrPredicate.True
    else -> KrPredicate.Not(exp)
}

/** Checks if a given expression is equal to true */
context(_: KrExpressionContext)
public fun isTrue(predicate: KrExpression<Boolean>): KrPredicate =
    KrPredicate.IsTrue(predicate)

/** Checks if a given expression is equal to false */
context(_: KrExpressionContext)
public fun isFalse(predicate: KrExpression<Boolean>): KrPredicate =
    KrPredicate.IsFalse(predicate)
