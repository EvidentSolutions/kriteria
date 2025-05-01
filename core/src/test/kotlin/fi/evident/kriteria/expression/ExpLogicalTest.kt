package fi.evident.kriteria.expression

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class ExpLogicalTest {

    @Test
    fun `always true or false`() = withTestExpressionContext {
        assertThat(alwaysTrue().isAlwaysTrue()).isTrue()
        assertThat(alwaysTrue().isAlwaysFalse()).isFalse()
        assertThat(alwaysFalse().isAlwaysTrue()).isFalse()
        assertThat(alwaysFalse().isAlwaysFalse()).isTrue()
    }

    @Nested
    inner class `optimization of conjunctions and disjunctions`() {

        @Test
        fun `empty disjunctions are constants`() = withTestExpressionContext {
            assertThat(and()).isEqualTo(alwaysTrue())
            assertThat(or()).isEqualTo(alwaysFalse())
        }

        @Test
        fun `always true elements are dropped from ands`() = withTestExpressionContext {
            val p1 = isNotNull(literal("foo"))
            val p2 = literal("foo") isEqualTo "bar"

            assertThat(and(alwaysTrue())).isEqualTo(alwaysTrue())
            assertThat(and(alwaysTrue(), p1)).isEqualTo(p1)
            assertThat(and(alwaysTrue(), p1, alwaysTrue(), p2, alwaysTrue())).isEqualTo(and(p1, p2))
        }

        @Test
        fun `always false element causes and to be false`() = withTestExpressionContext {
            val p1 = isNotNull(literal("foo"))
            val p2 = literal("foo") isEqualTo "bar"

            assertThat(and(alwaysFalse())).isEqualTo(alwaysFalse())
            assertThat(and(alwaysFalse(), p1)).isEqualTo(alwaysFalse())
            assertThat(and(p1, p2, alwaysFalse())).isEqualTo(alwaysFalse())
        }

        @Test
        fun `always false elements are dropped from ors`() = withTestExpressionContext {
            val p1 = isNotNull(literal("foo"))
            val p2 = literal("foo") isEqualTo "bar"

            assertThat(or(alwaysFalse())).isEqualTo(alwaysFalse())
            assertThat(or(alwaysFalse(), p1)).isEqualTo(or(p1))
            assertThat(or(alwaysFalse(), p1, alwaysFalse(), p2, alwaysFalse())).isEqualTo(or(p1, p2))
        }

        @Test
        fun `always true element causes or to be true`() = withTestExpressionContext {
            val p1 = isNotNull(literal("foo"))
            val p2 = literal("foo") isEqualTo "bar"

            assertThat(or(alwaysTrue())).isEqualTo(alwaysTrue())
            assertThat(or(alwaysTrue(), p1)).isEqualTo(alwaysTrue())
            assertThat(or(p1, p2, alwaysTrue())).isEqualTo(alwaysTrue())
        }
    }
}
