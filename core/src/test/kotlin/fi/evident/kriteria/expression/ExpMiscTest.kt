package fi.evident.kriteria.expression

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.annotations.DelicateCriteriaApi
import kotlin.test.Test

class ExpMiscTest {

    @Test
    @OptIn(DelicateCriteriaApi::class)
    fun `casting expressions is a no-op`() {
        val exp = LiteralExpression(42)

        assertThat(exp.cast(String::class)).isSameInstanceAs(exp)
    }
}
