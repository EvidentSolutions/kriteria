package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.isEqualTo
import fi.evident.kriteria.expression.isNull
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Department
import fi.evident.kriteria.test.gen.employees
import fi.evident.kriteria.test.gen.name
import kotlin.test.Test

@DatabaseTest
class SetJoinsTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `inner join set`() = transactionalTest(db) {
        val departments = em.findAll<Department> {
            val dept = from(Department)
            val e = innerJoinSet(dept.employees)

            where(e.name isEqualTo data.john.name)

            select(dept)
        }

        assertThat(departments.map { it.id }).containsExactly(data.cyberdyneSales.id)
    }

    @Test
    fun `left join set`() = transactionalTest(db) {
        val departments = em.findAll<Department> {
            val dept = from(Department)
            val e = leftJoinSet(dept.employees)

            where(isNull(e))
            select(dept)
        }

        assertThat(departments.map { it.id }).doesNotContain(data.cyberdyneSales.id)
    }
}
