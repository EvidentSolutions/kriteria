
package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.findAllByEntity
import fi.evident.kriteria.jpa.update
import fi.evident.kriteria.test.db.*
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.*
import kotlin.test.Test

@DatabaseTest
class UpdateTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun update() = testWithDatabaseContext(db) {
        transactionally {
            // This should update the existing row
            assertThat(em.update(Employee) {
                set(it.name, "Fred Bar")
                where(it.department.name isEqualTo data.acmeHr.name)
            }).isEqualTo(1)

            // This should not touch the row
            assertThat(em.update(Employee) {
                set(it.name, "Fred Baz")
                where(alwaysFalse(), alwaysTrue())
            }).isEqualTo(0)
        }

        transactionally {
            val fred = em.findRequired(Employee, data.fred.id)
            assertThat(fred.name).isEqualTo("Fred Bar")
        }
    }

    @Test
    fun `update with complex conditions`() = testWithDatabaseContext(db) {
        transactionally {
            // Update with multiple field changes
            val updatedCount = em.update(Employee) {
                set(it.name, "Updated Name")
                set(it.salary, 4500)
                where(it.id isEqualTo data.barry.id)
            }

            assertThat(updatedCount).isEqualTo(1)
        }

        transactionally {
            val barry = em.findRequired(Employee, data.barry.id)
            assertThat(barry.name).isEqualTo("Updated Name")
            assertThat(barry.salary).isEqualTo(4500)
        }
    }

    @Test
    fun `bulk update with OR condition`() = testWithDatabaseContext(db) {
        transactionally {
            // Update multiple records with OR condition
            val updatedCount = em.update(Employee) {
                set(it.salary, 7000)
                where(
                    or(
                        it.name isEqualTo data.john.name,
                        it.name isEqualTo data.sarah.name
                    )
                )
            }

            assertThat(updatedCount).isEqualTo(2)
        }

        transactionally {
            val cyberdyneEmployees = em.findAllByEntity(Employee) {
                it.department.id isEqualTo data.cyberdyneSales.id
            }

            assertThat(cyberdyneEmployees).hasSize(2)
            cyberdyneEmployees.forEach { employee ->
                assertThat(employee.salary).isEqualTo(7000)
            }
        }
    }

    @Test
    fun `update with nested conditions`() = testWithDatabaseContext(db) {
        transactionally {
            // Update using nested join conditions
            val updatedCount = em.update(Employee) {
                set(it.salary, it.salary + 1000)
                where(it.department.company.name isEqualTo data.acme.name)
            }

            assertThat(updatedCount).isEqualTo(3) // All employees from ACME
        }

        transactionally {
            val acmeEmployees = em.findAllByEntity(Employee) {
                it.department.company.id isEqualTo data.acme.id
            }

            assertThat(acmeEmployees).hasSize(3)
            for (employee in acmeEmployees) {
                val originalEmployee = data.employees.find { it.id == employee.id }!!
                assertThat(employee.salary).isEqualTo(originalEmployee.salary + 1000)
            }
        }
    }
}
