package fi.evident.kriteria.expression

import fi.evident.kriteria.annotations.DelicateCriteriaApi
import kotlin.reflect.KClass

/**
 * Creates an expression that constructs the given class give parameters.
 *
 * Do not call this directly, but use `@CriteriaConstructor` to created typed constructors.
 */
@DelicateCriteriaApi
context(_: KrExpressionContext)
public fun <T : Any> constructUnsafe(type: KClass<T>, vararg args: KrSelection<*>): KrSelection<T> =
    Construct(type, args.asList())
