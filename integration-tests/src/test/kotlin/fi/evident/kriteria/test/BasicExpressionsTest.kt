package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.evaluateExpression
import fi.evident.kriteria.test.db.transactionalTest
import org.junit.jupiter.api.Nested
import kotlin.test.Test

@DatabaseTest
class BasicExpressionsTest(private val db: DatabaseContext) {

    @Nested
    inner class `common operators` {
        @Test
        fun `check isNull`() = transactionalTest(db) {
            assertThat(evaluateExpression { isNull(nullLiteral<String>()) }).isTrue()
            assertThat(evaluateExpression { isNull(literal("foo")) }).isFalse()
        }

        @Test
        fun `check isNotNull`() = transactionalTest(db) {
            assertThat(evaluateExpression { isNotNull(nullLiteral<String>()) }).isFalse()
            assertThat(evaluateExpression { isNotNull(literal("foo")) }).isTrue()
        }
    }

    @Nested
    inner class `string expression` {

        @Test
        fun `substring without length`() = transactionalTest(db) {
            for (offset in listOf(0, 1, 2, 3, 4, 5))
                assertThat(evaluateExpression { literal("Hello").substring(offset) })
                    .isEqualTo("Hello".substring(offset))
        }

        @Test
        fun `substring with length`() = transactionalTest(db) {
            for (range in listOf(1..<4, 0..<5, 2..<3, 5..<5))
                assertThat(evaluateExpression { literal("Hello").substring(range) })
                    .isEqualTo("Hello".substring(range))
        }

        @Test
        fun `substring overloads`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("Hello").substring(1) }).isEqualTo("ello")
            assertThat(evaluateExpression { literal("Hello").substring(literal(1)) }).isEqualTo("ello")

            assertThat(evaluateExpression { literal("Hello").substring(1, 2) }).isEqualTo("e")
            assertThat(evaluateExpression { literal("Hello").substring(1, literal(2)) }).isEqualTo("e")
            assertThat(evaluateExpression { literal("Hello").substring(literal(1), 2) }).isEqualTo("e")
            assertThat(evaluateExpression { literal("Hello").substring(literal(1), literal(2)) }).isEqualTo("e")
        }

        @Test
        fun `case conversions`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("Foo").toUppercase() }).isEqualTo("FOO")
            assertThat(evaluateExpression { literal("Foo").toLowercase() }).isEqualTo("foo")
        }

        @Test
        fun `string startsWith`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("Foo") startsWith "F" }).isTrue()
            assertThat(evaluateExpression { literal("Foo") startsWith "Bar" }).isFalse()
        }

        @Test
        fun `string endsWith`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("Foo") endsWith "o" }).isTrue()
            assertThat(evaluateExpression { literal("Foo") endsWith "a" }).isFalse()
        }

        @Test
        fun `like operation`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("Fred Foo") like "Fr%oo" }).isTrue()
            assertThat(evaluateExpression { literal("Fred Foo") notLike "Fr%oo" }).isFalse()
        }

        @Test
        fun `doesNotContains operation`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("My string") contains "str" }).isTrue()
            assertThat(evaluateExpression { literal("My string") contains "foo" }).isFalse()
            assertThat(evaluateExpression { literal("My string") doesNotContain "str" }).isFalse()
            assertThat(evaluateExpression { literal("My string") doesNotContain "foo" }).isTrue()
        }

        @Test
        fun `concat operation`() = transactionalTest(db) {
            assertThat(evaluateExpression { concat("My", concat(literal(" "), "string")) })
                .isEqualTo("My string")
        }
    }

    @Nested
    inner class `numeric operators` {

        @Test
        fun addition() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(24) + 123 }).isEqualTo(147)
            assertThat(evaluateExpression { 24 + literal(123) }).isEqualTo(147)
            assertThat(evaluateExpression { literal(24) + literal(123) }).isEqualTo(147)
        }

        @Test
        fun subtraction() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(123) - 42 }).isEqualTo(81)
            assertThat(evaluateExpression { 123 - literal(42) }).isEqualTo(81)
            assertThat(evaluateExpression { literal(123) - literal(42) }).isEqualTo(81)
        }

        @Test
        fun `unary minus`() = transactionalTest(db) {
            assertThat(evaluateExpression { -literal(123) }).isEqualTo(-123)
        }

        @Test
        fun multiplication() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(24) * 123 }).isEqualTo(2952)
            assertThat(evaluateExpression { 24 * literal(123) }).isEqualTo(2952)
            assertThat(evaluateExpression { literal(24) * literal(123) }).isEqualTo(2952)
        }

        @Test
        fun division() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(42) / 2 }).isEqualTo(21)
            assertThat(evaluateExpression { 42 / literal(2) }).isEqualTo(21)
            assertThat(evaluateExpression { literal(42) / literal(2) }).isEqualTo(21)
        }

        @Test
        fun modulo() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(42) % 11 }).isEqualTo(9)
            assertThat(evaluateExpression { 42 % literal(11) }).isEqualTo(9)
            assertThat(evaluateExpression { literal(42) % literal(11) }).isEqualTo(9)
        }

        @Test
        fun sqrt() = transactionalTest(db) {
            assertThat(evaluateExpression { sqrt(literal(123)) }).isWithin(0.00001).of(11.09053)
        }

        @Test
        fun sign() = transactionalTest(db) {
            assertThat(evaluateExpression { sign(literal(-100.0)) }).isEqualTo(-1)
            assertThat(evaluateExpression { sign(literal(-1)) }).isEqualTo(-1)
            assertThat(evaluateExpression { sign(literal(0)) }).isEqualTo(0)
            assertThat(evaluateExpression { sign(literal(1)) }).isEqualTo(1)
            assertThat(evaluateExpression { sign(literal(100)) }).isEqualTo(1)
        }

        @Test
        fun ceiling() = transactionalTest(db) {
            assertThat(evaluateExpression { ceil(literal(3.2)) }).isEqualTo(4.0)
            assertThat(evaluateExpression { ceil(literal(3.7)) }).isEqualTo(4.0)
            assertThat(evaluateExpression { ceil(literal(-3.2)) }).isEqualTo(-3.0)
            assertThat(evaluateExpression { ceil(literal(-3.7)) }).isEqualTo(-3.0)
        }

        @Test
        fun floor() = transactionalTest(db) {
            assertThat(evaluateExpression { floor(literal(3.2)) }).isEqualTo(3.0)
            assertThat(evaluateExpression { floor(literal(3.7)) }).isEqualTo(3.0)
            assertThat(evaluateExpression { floor(literal(-3.2)) }).isEqualTo(-4.0)
            assertThat(evaluateExpression { floor(literal(-3.7)) }).isEqualTo(-4.0)
        }

        @Test
        fun exp() = transactionalTest(db) {
            assertThat(evaluateExpression { exp(literal(1.0)) }).isWithin(0.00001).of(2.71828)
            assertThat(evaluateExpression { exp(literal(0.0)) }).isWithin(0.00001).of(1.0)
        }

        @Test
        fun ln() = transactionalTest(db) {
            assertThat(evaluateExpression { ln(literal(2.71828)) }).isWithin(0.00001).of(1.0)
            assertThat(evaluateExpression { ln(literal(1.0)) }).isWithin(0.00001).of(0.0)
        }

        @Test
        fun power() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(2.0).pow(literal(3.0)) }).isWithin(0.00001).of(8.0)
            assertThat(evaluateExpression { literal(3.0).pow(2.0) }).isWithin(0.00001).of(9.0)
            assertThat(evaluateExpression { 4.0.pow(literal(0.5)) }).isWithin(0.00001).of(2.0)
        }
    }

    @Nested
    inner class `null expressions` {

        @Test
        fun `coalesce operation`() = transactionalTest(db) {
            assertThat(evaluateExpression { coalesce(literal("foo"), "bar") }).isEqualTo("foo")
            assertThat(evaluateExpression { coalesce(nullLiteral<String>(), "bar") }).isEqualTo("bar")
        }

        @Test
        fun `nullIf operation`() = transactionalTest(db) {
            assertThat(evaluateExpression { nullIf(literal("foo"), "bar") }).isEqualTo("foo")
            assertThat(evaluateExpression { nullIf(literal("foo"), "foo") }).isNull()
            assertThat(evaluateExpression { nullIf(nullLiteral<String>(), "bar") }).isNull()
        }
    }


    @Nested
    inner class `type coercions` {

        @Test
        fun `converting string to int`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("123").asInteger() }).isEqualTo(123)
        }

        @Test
        fun `converting string to long`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("123").asLong() }).isEqualTo(123)
        }

        @Test
        fun `converting string to double`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal("123.4").asDouble() }).isEqualTo(123.4)
        }

        @Test
        fun `converting integer to string`() = transactionalTest(db) {
            assertThat(evaluateExpression { literal(123).asString() }).isEqualTo("123")
        }
    }
}

