package fi.evident.kriteria.jpa.translation

import fi.evident.kriteria.expression.KrWindow

context(ctx: TranslationContext)
internal fun translateWindow(window: KrWindow) {
    val translated = hibernateCb("window functions").createWindow()

    if (window.partitionBy != null)
        translated.partitionBy(*window.partitionBy.map { it.translate() }.toTypedArray())

    if (window.orderBy != null)
        translated.orderBy(*window.orderBy.translateOrder().toTypedArray())

    window.translated = translated
}
