@file:OptIn(DelicateCriteriaApi::class)

package fi.evident.kriteria.expression

import fi.evident.kriteria.annotations.DelicateCriteriaApi

/** Returns the sum of expression over the given window */
context(_: KrExpressionContext)
public fun KrWindow.sum(exp: KrExpression<out Number>): KrExpression<Long> =
    callAggregateFunction<Long>("sum", exp)

/** Returns the mean of the expression over the given window */
context(_: KrExpressionContext)
public fun KrWindow.avg(exp: KrExpression<out Number>): KrExpression<Double> =
    callAggregateFunction<Double>("avg", exp)
