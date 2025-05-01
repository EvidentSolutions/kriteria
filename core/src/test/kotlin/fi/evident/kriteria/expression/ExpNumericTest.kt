package fi.evident.kriteria.expression

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class ExpNumericTest {

    @Test
    fun `literal additions get optimized`() = withTestExpressionContext {
        assertThat(literal(1) + literal(2)).isEqualTo(literal(3))
    }

    @Test
    fun `literal subtractions get optimized`() = withTestExpressionContext {
        assertThat(literal(3) - literal(2)).isEqualTo(literal(1))
    }

    @Test
    fun `literal multiplications get optimized`() = withTestExpressionContext {
        assertThat(literal(3) * literal(7)).isEqualTo(literal(21))
    }
}
