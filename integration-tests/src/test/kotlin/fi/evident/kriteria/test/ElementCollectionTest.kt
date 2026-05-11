package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.contains
import fi.evident.kriteria.expression.isEmpty
import fi.evident.kriteria.expression.isEqualTo
import fi.evident.kriteria.expression.isNull
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.attributes
import fi.evident.kriteria.test.gen.nicknames
import kotlin.test.Test

@DatabaseTest
class ElementCollectionTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `inner join on element collection`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)
            val nickname = innerJoinSet(emp.nicknames)

            where(nickname isEqualTo "JC")
            select(emp)
        }

        assertThat(employees.map { it.id }).containsExactly(data.john.id)
    }

    @Test
    fun `left join on element collection`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)
            val nickname = leftJoinSet(emp.nicknames)

            where(isNull(nickname))
            select(emp)
        }

        assertThat(employees.map { it.id }).doesNotContain(data.john.id)
    }

    @Test
    fun `collection contains element`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)
            where(emp.nicknames contains "The Chosen One")
            select(emp)
        }

        assertThat(employees.map { it.id }).containsExactly(data.john.id)
    }

    @Test
    fun `collection is empty`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)
            where(emp.nicknames.isEmpty)
            select(emp)
        }

        assertThat(employees.map { it.id }).doesNotContain(data.john.id)
    }

    @Test
    fun `inner join on map element collection`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)
            val attr = innerJoinMap(emp.attributes)

            where(
                attr.key isEqualTo "role",
                attr.value isEqualTo "hero"
            )
            select(emp)
        }

        assertThat(employees.map { it.id }).containsExactly(data.john.id)
    }

    @Test
    fun `left join on map element collection`() = transactionalTest(db) {
        val employees = em.findAll<Employee> {
            val emp = from(Employee)
            val attr = leftJoinMap(emp.attributes)

            where(isNull(attr))
            select(emp)
        }

        assertThat(employees.map { it.id }).doesNotContain(data.john.id)
    }
}
