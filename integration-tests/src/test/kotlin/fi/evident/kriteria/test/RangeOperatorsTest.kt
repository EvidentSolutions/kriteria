package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.evaluateExpression
import fi.evident.kriteria.test.db.transactionalTest
import kotlin.test.Test

@DatabaseTest
class RangeOperatorsTest(private val db: DatabaseContext) {

    @Test
    fun `inside inclusive ranges`() = transactionalTest(db) {
        assertThat(evaluateExpression<Boolean> { literal(35) isInRange literal(35)..literal(45) }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(40) isInRange literal(35)..literal(45) }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(45) isInRange literal(35)..literal(45) }).isTrue()
    }

    @Test
    fun `outside inclusive ranges`() = transactionalTest(db) {
        assertThat(evaluateExpression<Boolean> { literal(30) isInRange literal(35)..literal(45) }).isFalse()
        assertThat(evaluateExpression<Boolean> { literal(34) isInRange literal(35)..literal(45) }).isFalse()

        assertThat(evaluateExpression<Boolean> { literal(46) isInRange literal(35)..literal(45) }).isFalse()
        assertThat(evaluateExpression<Boolean> { literal(50) isInRange literal(35)..literal(45) }).isFalse()
    }

    @Test
    fun `inside exclusive ranges`() = transactionalTest(db) {
        assertThat(evaluateExpression<Boolean> { literal(35) isInRange literal(35)..<literal(45) }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(40) isInRange literal(35)..<literal(45) }).isTrue()
    }

    @Test
    fun `outside exclusive ranges`() = transactionalTest(db) {
        assertThat(evaluateExpression<Boolean> { literal(30) isInRange literal(35)..<literal(45) }).isFalse()
        assertThat(evaluateExpression<Boolean> { literal(34) isInRange literal(35)..<literal(45) }).isFalse()

        assertThat(evaluateExpression<Boolean> { literal(45) isInRange literal(35)..<literal(45) }).isFalse()
        assertThat(evaluateExpression<Boolean> { literal(50) isInRange literal(35)..<literal(45) }).isFalse()
    }

    @Test
    fun `between range`() = transactionalTest(db) {
        assertThat(evaluateExpression<Boolean> { literal(34) between 35..45 }).isFalse()
        assertThat(evaluateExpression<Boolean> { literal(35) between 35..45 }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(40) between 35..45 }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(45) between 35..45 }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(46) between 35..45 }).isFalse()
    }

    @Test
    fun `range overloads`() = transactionalTest(db) {
        assertThat(evaluateExpression<Boolean> { literal(40) isInRange 35..45 }).isTrue()
        assertThat(evaluateExpression<Boolean> { literal(40) isInRange 35..<45 }).isTrue()

        assertThat(evaluateExpression<Boolean> { 40 isInRange literal(35)..<literal(45) }).isTrue()
        assertThat(evaluateExpression<Boolean> { 40 isInRange literal(35)..literal(45) }).isTrue()
    }
}
