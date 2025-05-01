package fi.evident.kriteria.expression

fun withTestExpressionContext(callback: context(KrExpressionContext) () -> Unit) {
    withDefaultContexts { callback() }
}
