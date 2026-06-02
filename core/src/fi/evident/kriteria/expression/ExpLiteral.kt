package fi.evident.kriteria.expression

import kotlin.reflect.KClass

/** Returns an expression representing given literal value */
context(_: KrExpressionContext)
public fun <T> literal(v: T?): KrExpression<T> =
    LiteralExpression(v)

/** Returns a null literal for type `T` */
context(_: KrExpressionContext)
public inline fun <reified T : Any> nullLiteral(): KrExpression<T> =
    nullLiteral(T::class)

/** Returns a null literal for the given type */
context(_: KrExpressionContext)
public fun <T : Any> nullLiteral(cl: KClass<T>): KrExpression<T> =
    NullLiteralExpression(cl)

