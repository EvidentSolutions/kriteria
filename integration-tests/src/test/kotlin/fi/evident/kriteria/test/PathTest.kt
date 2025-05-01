package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Department
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.*
import kotlin.test.Test

@DatabaseTest
class PathTest(private val db: DatabaseContext) {

    @Test
    fun `check string representations of paths`() = transactionalTest(db) {
        em.findAll<Department> {
            val d = from(Department)
            select(d)

            assertThat(d.toString()).isEqualTo("Department")
            assertThat(d.name.toString()).isEqualTo("Department.name")
            assertThat(d.company.toString()).isEqualTo("Department.company")
            assertThat(d.company.name.toString()).isEqualTo("Department.company.name")
            assertThat(d.employees.toString()).isEqualTo("Department.employees")

            val e = innerJoinSet(d.employees)
            assertThat(e.toString()).isEqualTo("Department.employees")
            assertThat(e.name.toString()).isEqualTo("Department.employees.name")
            assertThat(e.projectRoles.toString()).isEqualTo("Department.employees.projectRoles")

            val roles = innerJoinMap(e.projectRoles)
            assertThat(roles.toString()).isEqualTo("Department.employees.projectRoles")
            assertThat(roles.key.toString()).isEqualTo("Department.employees.projectRoles.key")
            assertThat(roles.value.toString()).isEqualTo("Department.employees.projectRoles.value")
        }
    }

    @Test
    fun `get properties by KProperty reference`() = transactionalTest(db) {
        em.findAll<Employee> {
            val e = from(Employee)
            select(e)

            assertThat(e.getBasic(Employee::name)).isEqualTo(e.name)
            assertThat(e.getManyToOne(Employee::department)).isEqualTo(e.department)
        }
    }
}
