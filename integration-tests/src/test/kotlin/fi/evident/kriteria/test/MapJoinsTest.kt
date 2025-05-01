package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.isEqualTo
import fi.evident.kriteria.expression.isNull
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.projectRoles
import kotlin.test.Test

@DatabaseTest
class MapJoinsTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `find by map`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)

            val role = innerJoinMap(emp.projectRoles)
            where(
                role.key isEqualTo data.skynet,
                role.value isEqualTo data.salesManager
            )
            select(emp)
        }

        assertThat(employees.map { it.id }).containsExactly(data.john.id)
    }

    @Test
    fun `find by left join`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)

            val role = leftJoinMap(emp.projectRoles)
            where(isNull(role))
            select(emp)
        }

        assertThat(employees.map { it.id }).doesNotContain(data.john.id)
    }
}
