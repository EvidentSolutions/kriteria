package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.findSingle
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.department
import fi.evident.kriteria.test.gen.id
import fi.evident.kriteria.test.gen.name
import fi.evident.kriteria.test.gen.salary
import kotlin.test.Test

@DatabaseTest
class AggregateQueriesTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `count query`() = transactionalTest(db) {
        val count = em.findSingle<Long> {
            from(Employee)
            select(count())
        }

        assertThat(count).isEqualTo(data.employees.size.toLong())
    }

    @Test
    fun `count distinct query`() = transactionalTest(db) {
        val distinctDepartmentCount = em.findSingle<Long> {
            val e = from(Employee)
            select(countDistinct(e.department.id))
        }

        assertThat(distinctDepartmentCount).isEqualTo(3)
    }

    @Test
    fun `sum query`() = transactionalTest(db) {
        val totalSalary = em.findSingle<Int> {
            val e = from(Employee)
            select(sum(e.salary))
        }

        val expectedTotal = data.employees.sumOf { it.salary }
        assertThat(totalSalary).isEqualTo(expectedTotal)
    }

    @Test
    fun `sum as long`() = transactionalTest(db) {
        val totalSalary = em.findSingle<Long> {
            val e = from(Employee)
            select(sumAsLong(e.salary))
        }

        val expectedTotal = data.employees.sumOf { it.salary }
        assertThat(totalSalary).isEqualTo(expectedTotal)
    }

    @Test
    fun `sum as double`() = transactionalTest(db) {
        val totalSalary = em.findSingle<Double> {
            from(Employee)
            select(sumAsDouble(literal(0.1f)))
        }

        assertThat(totalSalary).isWithin(0.00001).of(0.1 * data.employees.size)
    }

    @Test
    fun `max query`() = transactionalTest(db) {
        val maxSalary = em.findSingle {
            val e = from(Employee)
            select(max(e.salary))
        }

        val expectedMax = data.employees.maxOf { it.salary }
        assertThat(maxSalary).isEqualTo(expectedMax)
    }

    @Test
    fun `avg query`() = transactionalTest(db) {
        val avgSalary = em.findSingle {
            val e = from(Employee)
            select(avg(e.salary.asDouble()))
        }

        val expectedAvg = data.employees.map { it.salary }.average()
        assertThat(avgSalary).isWithin(0.00001).of(expectedAvg)
    }

    @Test
    fun `min query`() = transactionalTest(db) {
        val minSalary = em.findSingle {
            val e = from(Employee)
            select(min(e.salary))
        }

        val expectedMin = data.employees.minOf { it.salary }
        assertThat(minSalary).isEqualTo(expectedMin)
    }

    @Test
    fun `countIf query`() = transactionalTest(db) {
        val count = em.findSingle<Long> {
            val e = from(Employee)
            select(countMatches(e.name startsWith "B"))
        }

        val expected = data.employees.count { it.name.startsWith("B") }
        assertThat(count).isEqualTo(expected)
    }
}
