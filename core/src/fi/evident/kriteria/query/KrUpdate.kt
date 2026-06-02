package fi.evident.kriteria.query

import fi.evident.kriteria.expression.*

internal class KrUpdate<T: Any>(
    val root: KrRoot<T>,
    val bindings: List<Binding<*>>,
    val restriction: KrPredicate,
) {
    class Binding<T>(val property: KrPropertyRef<T>, val expr: KrExpression<out T>)
}

/**
 * Context for building updates.
 */
public class KrUpdateBuilder<T : Any>(private val root: KrRoot<T>) {

    private val bindings = mutableListOf<KrUpdate.Binding<*>>()
    private var restriction: KrPredicate? = null

    public fun <X> set(property: KrPropertyRef<X>, value: X?) {
        bindings += KrUpdate.Binding(property, LiteralExpression(value))
    }

    public fun <X> set(property: KrPropertyRef<X>, expr: KrExpression<out X>) {
        bindings += KrUpdate.Binding(property, expr)
    }

    public fun where(restriction: KrPredicate) {
        check(this.restriction == null) { "where already called" }

        this.restriction = restriction
    }

    public fun where(first: KrPredicate, vararg rest: KrPredicate) {
        withDefaultContexts {
            where(and(first, *rest))
        }
    }

    internal fun build(): KrUpdate<T> = KrUpdate(
        root = root,
        restriction = restriction ?: error("where must be called"),
        bindings = bindings,
    )
}

