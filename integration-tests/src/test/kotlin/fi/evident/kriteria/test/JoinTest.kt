package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.*
import fi.evident.kriteria.test.entity.Department
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test

@DatabaseTest
class JoinTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `simple join`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val e = from(Employee)
            val d = innerJoin(e.department)

            where(d.name isEqualTo data.acmeRd.name)
            select(e)
        }

        assertThat(employees.map { it.id }).containsExactly(data.belle.id, data.barry.id)
    }

    @Test
    fun `join on a join`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val e = from(Employee)
            val d = innerJoin(e.department)
            val c = innerJoin(d.company)

            where(c.name isEqualTo data.acme.name)
            select(e)
        }

        assertThat(employees).hasSize(3)
    }

    @Test
    fun `fetch join`() = testWithDatabaseContext(db) {
        val employees = transactionally {
            em.findAll<Employee> {
                val e = from(Employee)
                val d = fetch(e.department)
                fetch(d.company)
                select(e)
            }
        }

        // check that the departments and companies have been initialized
        val departments = employees.map { it.department.company }.toSet()
        assertThat(departments.map { it.name })
            .containsExactlyElementsIn(data.companies.map { it.name })
    }

    @Nested
    inner class `various joins` {

        private val allEmployeeIds = data.employees.map { it.id }
        private val employeesWithManagersIds = data.employees.filter { it.manager != null }.map { it.id }

        @Test
        fun `inner join drops null rows`() = transactionalTest(db) {
            val employeeIds = em.findAll<Employee> {
                val e = from(Employee)
                innerJoin(e.manager)
                select(e)
            }.map { it.id }

            assertThat(employeeIds).containsExactlyElementsIn(employeesWithManagersIds)
        }

        @Test
        fun `fetch drops null rows`() = transactionalTest(db) {
            val employeeIds = em.findAll<Employee> {
                val e = from(Employee)
                fetch(e.manager)
                select(e)
            }.map { it.id }

            assertThat(employeeIds).containsExactlyElementsIn(employeesWithManagersIds)
        }

        @Test
        fun `left join does not drop null rows`() = transactionalTest(db) {
            val employeeIds = em.findAll<Employee> {
                val e = from(Employee)
                leftJoin(e.manager)
                select(e)
            }.map { it.id }

            assertThat(employeeIds).containsExactlyElementsIn(allEmployeeIds)
        }

        @Test
        fun `fetchOptional join does not drop null rows`() = transactionalTest(db) {
            val employeeIds = em.findAll<Employee> {
                val e = from(Employee)
                fetchOptional(e.manager)
                select(e)
            }.map { it.id }

            assertThat(employeeIds).containsExactlyElementsIn(allEmployeeIds)
        }

        @Test
        fun `set joins`() = transactionalTest(db) {
            val employeeIds = em.findAll<Department> {
                val d = from(Department)
                fetchSet(d.employees)
                select(d)
            }.map { it.id }

            assertThat(employeeIds).containsExactlyElementsIn(data.departments.filter { it.employees.isNotEmpty() }.map { it.id})
        }

        @Test
        fun `optional set joins`() = transactionalTest(db) {
            val employeeIds = em.findAll<Department> {
                val d = from(Department)
                fetchSetOptional(d.employees)
                select(d)
            }.map { it.id }

            assertThat(employeeIds).containsExactlyElementsIn(data.departments.map { it.id})
        }
    }

    @Test
    fun `complex query with multiple conditions`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val e = from(Employee)
            val d = innerJoin(e.department)
            val c = innerJoin(d.company)

            where(
                and(
                    c.name isEqualTo data.acme.name,
                    or(
                        e.salary isGreaterThan 3500,
                        d.name isEqualTo data.acmeHr.name
                    )
                )
            )

            select(e)
            orderBy(desc(e.salary))
        }

        val expected = data.employees
            .filter {
                it.department.company.name == data.acme.name &&
                        (it.salary > 3500 || it.department.name == data.acmeHr.name)
            }
            .sortedByDescending { it.salary }
            .map { it.id }

        assertThat(employees.map { it.id }).containsExactlyElementsIn(expected).inOrder()
    }

    @Nested
    inner class `joins with arbitrary predicate` {

        @Test
        fun `inner joins`() = transactionalTest(db) {
            val employees = em.findAll<String> {
                val emp = from(Employee)
                val dept = innerJoin(emp, Department) { it.id isEqualTo emp.department.id }
                select(concat(emp.name, concat(" ", dept.name)))
            }

            val expected = data.employees.map { "${it.name} ${it.department.name}" }
            assertThat(employees).containsExactlyElementsIn(expected)
        }

        @Test
        fun `left joins`() = transactionalTest(db) {
            val employees = em.findAll<String> {
                val emp = from(Employee)
                val dept = leftJoin(emp, Department) {
                    and(
                        it.id isEqualTo emp.department.id,
                        it.id isNotEqualTo data.acmeHr.id
                    )
                }
                select(concat(emp.name, concat(" ", coalesce(dept.name, "-"))))
            }

            val expected = data.employees.map {
                if (it.department.id != data.acmeHr.id) "${it.name} ${it.department.name}" else "${it.name} -"
            }

            assertThat(employees).containsExactlyElementsIn(expected)
        }
    }
}
