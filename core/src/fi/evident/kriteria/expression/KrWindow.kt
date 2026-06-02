package fi.evident.kriteria.expression

import org.hibernate.query.criteria.JpaWindow

/**
 * Defines windows for window functions.
 */
public class KrWindow internal constructor(
    internal val partitionBy: List<KrExpression<*>>?,
    internal val orderBy: KrOrder?,
) {

    internal var translated: JpaWindow? = null
}

/**
 * Contexts for configuring built [KrWindow]s.
 */
public class KrWindowBuilder internal constructor()  {

    private var partitionBy: List<KrExpression<*>>? = null
    private var orderBy: KrOrder? = null

    internal fun build() = KrWindow(partitionBy, orderBy)

    public fun partitionBy(vararg expressions: KrExpression<*>) {
        check(partitionBy == null) { "partitionBy already called" }

        this.partitionBy = expressions.toList()
    }

    public fun orderBy(order: KrOrder) {
        check(orderBy == null) { "orderBy already called" }

        this.orderBy = order
    }
}

