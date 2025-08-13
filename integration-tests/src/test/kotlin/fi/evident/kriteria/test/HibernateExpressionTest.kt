@file:OptIn(DelicateCriteriaApi::class)

package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.annotations.DelicateCriteriaApi
import fi.evident.kriteria.expression.asc
import fi.evident.kriteria.expression.hibernate
import fi.evident.kriteria.jpa.findFirstOrNull
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.name
import kotlin.test.Test

@DatabaseTest
class HibernateExpressionTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `call hibernate expression`() = transactionalTest(db) {
        val result = em.findFirstOrNull {
            val employee = from(Employee)
            select(hibernate { cb.concat("Employee ", translate(employee.name)) })
            orderBy(asc(employee.name))
        }

        val first = data.employees.minOf { it.name }
        assertThat(result).isEqualTo("Employee $first")
    }
}
