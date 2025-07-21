package fi.evident.kriteria.expression

/** Checks if the expression evaluates to any of the given values */
context(_: KrExpressionContext)
public fun <T> KrExpression<T>.isAnyOf(vararg values: T): KrPredicate =
    isAnyOf(values.asList())

/** Checks if the expression evaluates to any of the given values */
context(_: KrExpressionContext)
public infix fun <T> KrExpression<T>.isAnyOf(values: Collection<T>): KrPredicate =
    KrPredicate.IsAnyOf(this, values)

context(_: KrExpressionContext)
public infix fun <T : Any> KrExpression<T>.isAnyOf(subQuery: KrSubquery<T>): KrPredicate =
    KrPredicate.ContainsSubquery(this, subQuery)

/** Checks if the collection contains the given element */
context(_: KrExpressionContext)
public infix fun <T> KrCollectionRef<T>.contains(element: KrExpression<T>): KrPredicate =
    KrPredicate.IsMember(element, this)

/** Checks if the collection contains the given element */
context(_: KrExpressionContext)
public infix fun <T> KrCollectionRef<T>.contains(element: T): KrPredicate =
    contains(literal(element))

/** Checks if the collection does not contain the given element */
context(_: KrExpressionContext)
public infix fun <T> KrCollectionRef<T>.doesNotContain(element: KrExpression<T>): KrPredicate =
    KrPredicate.IsNotMember(element, this)

/** Checks if the collection does not contain the given element */
context(_: KrExpressionContext)
public infix fun <T> KrCollectionRef<T>.doesNotContain(element: T): KrPredicate =
    doesNotContain(literal(element))

/** Checks if a given collection is empty */
context(_: KrExpressionContext)
public val KrCollectionRef<*>.isEmpty: KrPredicate
    get() = KrPredicate.IsEmpty(this)

/** Checks if a given collection is not empty */
context(_: KrExpressionContext)
public val KrCollectionRef<*>.isNotEmpty: KrPredicate
    get() = KrPredicate.IsNotEmpty(this)

/** Returns the size of the collection */
context(_: KrExpressionContext)
public val KrCollectionRef<*>.size: KrExpression<Int>
    get() = NumericExpression.Size(this)

