package fi.evident.kriteria.expression

import fi.evident.kriteria.query.RootSet
import kotlin.reflect.KClass

/**
 * Represents a subquery.
 */
public class KrSubquery<T : Any> internal constructor(
    internal val resultClass: KClass<T>,
    internal val roots: RootSet,
    internal val selection: KrExpression<T>,
    internal val distinct: Boolean,
    internal val restriction: KrPredicate?,
    internal val groupBy: List<KrExpression<*>>?,
) : KrExpression<T>()
