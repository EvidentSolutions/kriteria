package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.jpa.findSingle
import fi.evident.kriteria.query.exists
import fi.evident.kriteria.query.subquery
import fi.evident.kriteria.test.db.*
import fi.evident.kriteria.test.entity.Department
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.*
import kotlin.test.Test

@DatabaseTest
class SubqueriesTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `simple sub-queries`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val e = from(Employee)

            select(e)
            where(e.department isAnyOf subquery<Department> {
                val d = from(Department)
                where(d.name isEqualTo data.acmeHr.name)
                selectDistinct(d)
            })
        }

        assertThat(employees.map { it.id }).containsExactly(data.fred.id)
    }

    @Test
    fun `multiple restriction for sub-query`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val e = from(Employee)

            select(e)
            where(e.department isAnyOf subquery<Department> {
                val department = from(Department)
                val company = innerJoin(department.company)
                where(
                    company.name isEqualTo data.acme.name,
                    department.name.length isNotEqualTo 3
                )
                select(department)
            })
        }

        assertThat(employees.map { it.id }).containsExactly(data.fred.id)
    }

    @Test
    fun `exists queries`() = transactionalTest(db) {
        val departments = em.findAll<Department> {
            val d = from(Department)
            select(d)
            where(exists(Employee) { e ->
                and(
                    e.department.id isEqualTo d.id,
                    e.name isEqualTo data.fred.name
                )
            })
        }

        assertThat(departments.map { it.id }).containsExactly(data.fred.department.id)
    }

    @Test
    fun `contains queries`() = transactionalTest(db) {
        val fred = em.findRequired(Employee, data.fred.id)

        val department = em.findSingle<Department> {
            val d = from(Department)
            select(d)
            where(d.employees contains fred)
        }

        assertThat(department.id).isEqualTo(data.fred.department.id)
    }

    @Test
    fun `doesNotContain queries`() = transactionalTest(db) {
        val fred = em.findRequired(Employee, data.fred.id)

        val department = em.findAll<Department> {
            val d = from(Department)
            select(d)
            where(d.employees doesNotContain fred)
        }

        val otherDepartmentIds = data.departments.filter { it.id != data.fred.department.id }.map { it.id }
        assertThat(department.map { it.id }).containsExactlyElementsIn(otherDepartmentIds)
    }

    @Test
    fun `subquery with exists`() = transactionalTest(db) {
        // Find departments that have employees with salary > 4000
        val departmentsWithHighPaidEmployees = em.findAll<Department> {
            val d = from(Department)

            where(exists(Employee) { e ->
                and(
                    e.department.id isEqualTo d.id,
                    e.salary isGreaterThan 4000
                )
            })

            select(d)
        }

        val expected = data.departments
            .filter { dept ->
                data.employees.any {
                    it.department.id == dept.id && it.salary > 4000
                }
            }
            .map { it.id }

        assertThat(departmentsWithHighPaidEmployees.map { it.id }).containsExactlyElementsIn(expected)
    }
}
