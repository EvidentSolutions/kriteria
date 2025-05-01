package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.test.db.*
import kotlin.test.Test

@DatabaseTest
class RelationalOperatorsTest(private val db: DatabaseContext) {

    @Test
    fun equality() = transactionalTest(db) {
        matchesReference1({ x, y -> x == y }) { x, y -> x isEqualTo y }
        matchesReference2({ x, y -> x == y }) { x, y -> x isEqualTo y }
    }

    @Test
    fun inequality() = transactionalTest(db) {
        matchesReference1({ x, y -> x != y }) { x, y -> x isNotEqualTo y }
        matchesReference2({ x, y -> x != y }) { x, y -> x isNotEqualTo y }
    }

    @Test
    fun `less than`() = transactionalTest(db) {
        matchesReference1({ x, y -> x < y }) { x, y -> x isLessThan y }
        matchesReference2({ x, y -> x < y }) { x, y -> x isLessThan y }
    }

    @Test
    fun `less than or equal to`() = transactionalTest(db) {
        matchesReference1({ x, y -> x <= y }) { x, y -> x isLessThanOrEqualTo y }
        matchesReference2({ x, y -> x <= y }) { x, y -> x isLessThanOrEqualTo y }
    }

    @Test
    fun `greater than`() = transactionalTest(db) {
        matchesReference1({ x, y -> x > y }) { x, y -> x isGreaterThan y }
        matchesReference2({ x, y -> x > y }) { x, y -> x isGreaterThan y }
    }

    @Test
    fun `greater than or equal to operation`() = transactionalTest(db) {
        matchesReference1({ x, y -> x >= y }) { x, y -> x isGreaterThanOrEqualTo y }
        matchesReference2({ x, y -> x >= y }) { x, y -> x isGreaterThanOrEqualTo y }
    }

    companion object {
        context(tx: Tx)
        private fun matchesReferenceBase(
            reference: (Int, Int) -> Boolean,
            func: context(KrExpressionContext) (Int, Int) -> KrPredicate,
        ) {
            val nums = listOf(-1, 0, 1, 2, 3)

            for (x in nums)
                for (y in nums) {
                    val result = evaluateExpression { func(x, y) }
                    assertThat(result).isEqualTo(reference(x, y))
                }
        }

        context(tx: Tx)
        private fun matchesReference1(
            reference: (Int, Int) -> Boolean,
            func: context(KrExpressionContext) (KrExpression<Int>, KrExpression<Int>) -> KrPredicate,
        ) {
            matchesReferenceBase(reference) { x, y -> func(literal(x), literal(y)) }
        }

        context(tx: Tx)
        private fun matchesReference2(
            reference: (Int, Int) -> Boolean,
            func: context(KrExpressionContext) (KrExpression<Int>, Int) -> KrPredicate,
        ) {
            matchesReferenceBase(reference) { x, y -> func(literal(x), y) }
        }
    }
}
