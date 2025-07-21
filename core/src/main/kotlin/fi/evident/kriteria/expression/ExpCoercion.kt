package fi.evident.kriteria.expression

import fi.evident.kriteria.annotations.DelicateCriteriaApi
import kotlin.reflect.KClass

/** Forces this expression to be coerced to an integer type. */
context(_: KrExpressionContext)
internal fun <T : Any> KrExpression<*>.coerceTo(target: KClass<T>): KrExpression<T> =
    CoerceExpression(this, target)

/** Forces this expression to be coerced to an integer type. */
context(_: KrExpressionContext)
public fun KrExpression<*>.asInteger(): KrExpression<Int> = coerceTo(Int::class)

/** Forces this expression to be coerced to a long type. */
context(_: KrExpressionContext)
public fun KrExpression<*>.asLong(): KrExpression<Long> = coerceTo(Long::class)

/** Forces this expression to be coerced to a double type. */
context(_: KrExpressionContext)
public fun KrExpression<*>.asDouble(): KrExpression<Double> = coerceTo(Double::class)

/** Forces this expression to be coerced to a string type. */
context(_: KrExpressionContext)
public fun KrExpression<*>.asString(): KrExpression<String> = coerceTo(String::class)
/**
 * Casts this expression as an expression of the given type.
 *
 * The cast is not checked, so using the expressions may cause surprising errors.
 */
@Suppress("UNCHECKED_CAST")
@DelicateCriteriaApi
public fun <S : Any> KrExpression<*>.cast(type: KClass<S>): KrExpression<S> = this as KrExpression<S>
