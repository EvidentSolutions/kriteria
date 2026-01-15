package fi.evident.kriteria.expression

import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class SelectCaseTest {

    @Test
    fun `it is not allowed to call otherwise multiple times`() = withTestExpressionContext {
        val _ = selectCase {
            whenCase(alwaysTrue(), 1)
            otherwise(123)

            assertFailsWith<IllegalStateException> {
                otherwise(456)
            }
        }
    }

    @Test
    fun `when is required`() = withTestExpressionContext {
        assertFails {
            selectCase {
                otherwise(123)
            }
        }
    }

    @Test
    fun `otherwise is optional`() = withTestExpressionContext {
        val _ = selectCase {
            whenCase(alwaysTrue(), 1)
        }
    }
}
