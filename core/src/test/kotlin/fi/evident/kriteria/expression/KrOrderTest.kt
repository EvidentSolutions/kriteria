package fi.evident.kriteria.expression

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class KrOrderTest {

    @Test
    fun `ordering to list`() = withDefaultContexts {
        val e1 = literal(1)
        val e2 = literal(2)
        val e3 = literal(3)

        assertThat(asc(e1).toList())
            .isEqualTo(listOf(asc(e1)))

        assertThat((asc(e1) then asc(e2)).toList())
            .isEqualTo(listOf(asc(e1), asc(e2)))

        assertThat((asc(e1) then asc(e2) then desc(e3)).toList())
            .isEqualTo(listOf(asc(e1), asc(e2), desc(e3)))
    }
}
