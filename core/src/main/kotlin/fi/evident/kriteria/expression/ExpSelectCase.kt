package fi.evident.kriteria.expression

/** Build a select-case expression. See [SelectCaseBuilder] for details. */
context(_: KrExpressionContext)
public fun <T> selectCase(callback: SelectCaseBuilder<T>.() -> Unit): KrExpression<T> {
    val builder = SelectCaseBuilder<T>()
    builder.callback()
    return builder.build()
}

/** If `condition` is true, returns `trueResult`, otherwise returns `falseResult` */
context(_: KrExpressionContext)
public fun <T : Any> ifThenElse(
    condition: KrExpression<Boolean>,
    onTrue: KrExpression<T>,
    onFalse: KrExpression<T>
): KrExpression<T> =
    buildIfThenElse(condition, onTrue, onFalse)

/** If `condition` is true, returns `trueResult`, otherwise returns `falseResult` */
context(_: KrExpressionContext)
public fun <T : Any> ifThenElse(condition: KrExpression<Boolean>, onTrue: T, onFalse: T): KrExpression<T> =
    buildIfThenElse(condition, literal(onTrue), literal(onFalse))

/** If `condition` is true, returns `trueResult`, otherwise returns `falseResult` */
context(_: KrExpressionContext)
public fun <T : Any> ifThenElse(condition: KrExpression<Boolean>, onTrue: KrExpression<T>, onFalse: T): KrExpression<T> =
    buildIfThenElse(condition, onTrue, literal(onFalse))

/** If `condition` is true, returns `trueResult`, otherwise returns `falseResult` */
context(_: KrExpressionContext)
public fun <T : Any> ifThenElse(condition: KrExpression<Boolean>, onTrue: T, onFalse: KrExpression<T>): KrExpression<T> =
    buildIfThenElse(condition, literal(onTrue), onFalse)

private fun <T : Any> buildIfThenElse(
    condition: KrExpression<Boolean>,
    onTrue: KrExpression<T>,
    onFalse: KrExpression<T>
): KrExpression<T> =
    SelectCase(listOf(WhenCase(condition, onTrue)), onFalse)

/**
 * DSL marker annotation used to provide scoping for select-case expressions.
 */
// TODO @DslMarker
public annotation class SelectCaseDsl

/**
 * Builder for case expressions.
 *
 * See [selectCase].
 */
public class SelectCaseBuilder<T> {

    private val whenCases = mutableListOf<WhenCase<T>>()
    private var otherwise: KrExpression<T>? = null

    /** Adds a new when-case to the expression */
    context(_: KrExpressionContext)
    public fun whenCase(condition: KrExpression<Boolean>, result: T) {
        whenCases += WhenCase(condition, LiteralExpression(result))
    }

    /** Adds a new when-case to the expression */
    context(_: KrExpressionContext)
    public fun whenCase(condition: KrExpression<Boolean>, result: KrExpression<T>) {
        whenCases += WhenCase(condition, result)
    }

    /** Adds a default case to the expression */
    context(_: KrExpressionContext)
    public fun otherwise(result: KrExpression<T>) {
        check(otherwise == null) { "otherwise already set" }
        otherwise = result
    }

    /** Adds a default case to the expression */
    context(_: KrExpressionContext)
    public fun otherwise(result: T) {
        otherwise(literal(result))
    }

    internal fun build(): SelectCase<T> {
        check(whenCases.isNotEmpty()) { "at least one when-case must be specified" }
        return SelectCase(whenCases, otherwise)
    }
}

