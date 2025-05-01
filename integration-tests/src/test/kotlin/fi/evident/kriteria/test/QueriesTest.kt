package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.*
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Department
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test

@DatabaseTest
class QueriesTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `find all rows of a table`() = transactionalTest(db) {
        val employees = em.findAllByEntity(Employee) { alwaysTrue() }

        assertThat(employees.map { it.id }).containsExactlyElementsIn(data.employees.map { it.id })
    }

    @Test
    fun `count by predicate`() = transactionalTest(db) {
        val count = em.countByEntity(Employee) { it.name startsWith "B" }
        val expected = data.employees.count { it.name.startsWith("B") }
        assertThat(count).isEqualTo(expected)
    }

    @Test
    fun `find entities by predicate`() = transactionalTest(db) {
        val employees = em.findAllByEntity(Employee) { it.name startsWith "Fred" }

        assertThat(employees.map { it.id }).containsExactly(data.fred.id)
    }

    @Test
    fun `find custom projection`() = transactionalTest(db) {
        val names = em.findAll<String> {
            val e = from(Employee)
            where(e.name startsWith "Barry")
            select(e.name)
        }

        assertThat(names).containsExactly("Barry Bar")
    }

    @Test
    fun `find with custom order`() = transactionalTest(db) {
        val employees = em.findAllByEntity(Employee, order = { desc(name) }) {
            it.department.id isEqualTo data.acmeRd.id
        }

        assertThat(employees.map { it.name }).containsExactly(data.belle.name, data.barry.name).inOrder()
    }

    @Test
    fun `multiple order components`() = transactionalTest(db) {
        val employees = em.findAllByEntity(Employee, order = { desc(department.name) then asc(name) }) {
            alwaysTrue()
        }

        assertThat(employees.map { it.name }).containsExactly(
            data.john.name, data.sarah.name, data.barry.name, data.belle.name, data.fred.name
        ).inOrder()
    }

    @Nested
    inner class `multiple roots are separate` {

        @Test
        fun `multiple roots into same entity`() = transactionalTest(db) {
            val rows = em.findAll<String> {
                val e1 = from(Employee)
                val e2 = from(Employee)
                select(concat(e1.name, e2.name))
            }

            assertThat(rows).hasSize(data.employees.size * data.employees.size)
        }
    }

    @Test
    fun `select distinct`() = transactionalTest(db) {
        val departments = em.findAll<Department> {
            val e = from(Employee)
            selectDistinct(e.department)
        }

        val expected = data.employees.map { it.department.id }.toSet()
        assertThat(departments.map { it.id }).containsExactlyElementsIn(expected)
    }

    @Nested
    inner class MathematicalOperations {

        @Test
        fun `plus operation`() = transactionalTest(db) {
            val salaryPlusBonuses = em.findAll<Int> {
                val e = from(Employee)
                select(e.salary + 1000)
            }

            val expected = data.employees.map { it.salary + 1000 }
            assertThat(salaryPlusBonuses).containsExactlyElementsIn(expected)
        }

        @Test
        fun `minus operation`() = transactionalTest(db) {
            val salaryMinusTax = em.findAll<Int> {
                val e = from(Employee)
                select(e.salary - 500)
            }

            val expected = data.employees.map { it.salary - 500 }
            assertThat(salaryMinusTax).containsExactlyElementsIn(expected)
        }

        @Test
        fun `unary minus operation`() = transactionalTest(db) {
            val negativeSalaries = em.findAll<Int> {
                val e = from(Employee)
                select(-e.salary)
            }

            val expected = data.employees.map { -it.salary }
            assertThat(negativeSalaries).containsExactlyElementsIn(expected)
        }
    }

    @Nested
    inner class BooleanOperations {

        @Test
        fun `and operation`() = transactionalTest(db) {
            val employees = em.findAllByEntity(Employee) {
                and(
                    it.salary isGreaterThan 3000,
                    it.name contains "Connor"
                )
            }

            val expectedIds = data.employees
                .filter { it.salary > 3000 && it.name.contains("Connor") }
                .map { it.id }

            assertThat(employees.map { it.id }).containsExactlyElementsIn(expectedIds)
        }

        @Test
        fun `or operation`() = transactionalTest(db) {
            val employees = em.findAllByEntity(Employee) {
                or(
                    it.salary isGreaterThan 5000,
                    it.name contains "Fred"
                )
            }

            val expectedIds = data.employees
                .filter { it.salary > 5000 || it.name.contains("Fred") }
                .map { it.id }

            assertThat(employees.map { it.id }).containsExactlyElementsIn(expectedIds)
        }

        @Test
        fun `not operation`() = transactionalTest(db) {
            val employees = em.findAllByEntity(Employee) { not(it.salary isLessThan 4000) }

            val expectedIds = data.employees.filter { it.salary >= 4000 }.map { it.id }
            assertThat(employees.map { it.id }).containsExactlyElementsIn(expectedIds)
        }
    }

    @Nested
    inner class `test findFirstOrNull` {

        @Test
        fun `findFirstOrNull returns null for empty results`() = transactionalTest(db) {
            val result = em.findFirstOrNullByEntity(Employee) { alwaysFalse() }
            assertThat(result).isNull()
        }

        @Test
        fun `findFirstOrNull returns single value for single result`() = transactionalTest(db) {
            val result = em.findFirstOrNullByEntity(Employee) { it.id isEqualTo data.fred.id }
            assertThat(result?.id).isEqualTo(data.fred.id)
        }

        @Test
        fun `findFirstOrNull returns first value for multiple results`() = transactionalTest(db) {
            val result = em.findFirstOrNullByEntity(Employee, order = { asc(id) }) {
                it.id.isAnyOf(data.fred.id, data.john.id)
            }

            assertThat(result?.id).isEqualTo(data.fred.id)
        }
    }

    @Nested
    inner class `test contains` {

        @Test
        fun `contains returns true for existing rows`() = transactionalTest(db) {
            assertThat(em.containsEntity(Employee) { it.id isEqualTo data.fred.id }).isTrue()
            assertThat(em.containsEntity(Department) { alwaysTrue() }).isTrue()
        }

        @Test
        fun `simple contains tests`() = transactionalTest(db) {
            assertThat(em.containsEntity(Employee) { it.id isEqualTo 1234567 }).isFalse()
        }
    }

    @Nested
    inner class `collection predicates` {

        @Test
        fun `test isEmpty`() = transactionalTest(db) {
            val result = em.findAll<Department> {
                val d = from(Department)
                where(d.employees.isEmpty)
                select(d)
            }

            val expected = data.departments.filter { it.employees.isEmpty() }.map { it.id }
            assertThat(result.map { it.id }).containsExactlyElementsIn(expected)
        }

        @Test
        fun `test isNotEmpty`() = transactionalTest(db) {
            val result = em.findAll<Department> {
                val d = from(Department)
                where(d.employees.isNotEmpty)
                select(d)
            }

            val expected = data.departments.filter { it.employees.isNotEmpty() }.map { it.id }
            assertThat(result.map { it.id }).containsExactlyElementsIn(expected)
        }

        @Test
        fun `test size`() = transactionalTest(db) {
            val result = em.findAll<Int> {
                val d = from(Department)
                select(d.employees.size)
            }

            val expected = data.departments.map { it.employees.size }
            assertThat(result).containsExactlyElementsIn(expected)
        }
    }

    @Nested
    inner class `boolean predicates` {

        @Test
        fun `isTrue is true for true`() = transactionalTest(db) {
            val result = em.findFirstOrNull<String> {
                select(literal("foo"))
                where(isTrue(literal(true)))
            }

            assertThat(result).isEqualTo("foo")
        }

        @Test
        fun `isTrue is false for false`() = transactionalTest(db) {
            val result = em.findFirstOrNull<String> {
                select(literal("foo"))
                where(isTrue(literal(false)))
            }

            assertThat(result).isNull()
        }

        @Test
        fun `isFalse is false for true`() = transactionalTest(db) {
            val result = em.findFirstOrNull<String> {
                select(literal("foo"))
                where(isFalse(literal(true)))
            }

            assertThat(result).isNull()
        }

        @Test
        fun `isFalse is true for false`() = transactionalTest(db) {
            val result = em.findFirstOrNull<String> {
                select(literal("foo"))
                where(isFalse(literal(false)))
            }

            assertThat(result).isEqualTo("foo")
        }
    }
}
