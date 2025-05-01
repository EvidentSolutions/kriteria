package fi.evident.kriteria.expression

import fi.evident.kriteria.query.KrSubQueryContext

/**
 * Provides a context for constructing criteria expressions.
 *
 * See the various context methods in this package for actual operations.
 */
public abstract class KrExpressionContext internal constructor()

/**
 * Provides a context for specifying ordering in queries.
 *
 * This class is used as a context receiver for the [asc] and [desc] functions.
 */
public abstract class KrOrderByContext internal constructor()

internal object DefaultExpressionContext : KrExpressionContext()
internal object DefaultOrderByContext : KrOrderByContext()
internal object DefaultSubQueryContext : KrSubQueryContext()

internal inline fun <T> withDefaultContexts(block: context(KrExpressionContext, KrOrderByContext, KrSubQueryContext) () -> T): T =
    context(DefaultExpressionContext, DefaultOrderByContext, DefaultSubQueryContext) {
        block()
    }
