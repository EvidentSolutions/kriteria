package fi.evident.kriteria.expression

/**
 * Represents an ordering specification for a query.
 *
 * Orders can be combined using the [then] method to create multi-level ordering.
 */
public sealed class KrOrder {

    internal sealed class Simple : KrOrder()
    internal data class Asc(val exp: KrExpression<*>) : Simple()
    internal data class Desc(val exp: KrExpression<*>) : Simple()
    internal data class FollowedBy(val lhs: KrOrder, val rhs: KrOrder) : KrOrder()

    /**
     * Combines this order with another order to create a multi-level ordering.
     *
     * The resulting order will first order by this order, and then by the other order
     * for elements that are equal according to this order.
     */
    public infix fun then(other: KrOrder): KrOrder = FollowedBy(this, other)

    internal fun toList(): List<Simple> {
        val result = mutableListOf<Simple>()
        fun recurse(expr: KrOrder) {
            when (expr) {
                is Simple -> result += expr
                is FollowedBy -> {
                    recurse(expr.lhs)
                    recurse(expr.rhs)
                }
            }
        }
        recurse(this)
        return result
    }
}

/** Creates an ordering by the ascending value of the expression. */
context(_: KrOrderByContext)
public fun asc(x: KrExpression<*>): KrOrder =
    KrOrder.Asc(x)

/** Creates an ordering by the descending value of the expression. */
context(_: KrOrderByContext)
public fun desc(x: KrExpression<*>): KrOrder =
    KrOrder.Desc(x)
