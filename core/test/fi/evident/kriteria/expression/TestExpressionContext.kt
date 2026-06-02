package fi.evident.kriteria.expression

internal fun withTestExpressionContext(callback: context(KrExpressionContext) () -> Unit) {
    withDefaultContexts { callback() }
}
