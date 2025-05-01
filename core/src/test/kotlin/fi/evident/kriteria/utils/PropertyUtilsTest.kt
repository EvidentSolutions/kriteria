package fi.evident.kriteria.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PropertyUtilsTest {

    @Test
    fun `receiverClass returns correct class for property`() {
        assertEquals(TestClass::class, TestClass::value.receiverClass)
    }

    private class TestClass(val value: String)
}
