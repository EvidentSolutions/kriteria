package fi.evident.kriteria.expression

import fi.evident.kriteria.annotations.DelicateCriteriaApi
import kotlin.reflect.KClass

/** Calls a named function that has return type `T` with given arguments */
@DelicateCriteriaApi
context(_: KrExpressionContext)
public inline fun <reified T : Any> callFunction(name: String, vararg args: KrExpression<*>): KrExpression<T> =
    callFunction(name, T::class, *args)

/** Calls a named function that has given return type with given arguments */
@DelicateCriteriaApi
context(_: KrExpressionContext)
public fun <T : Any> callFunction(name: String, type: KClass<T>, vararg args: KrExpression<*>): KrExpression<T> =
    CallExpression(name, type, args.asList())
