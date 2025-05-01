package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.annotations.CriteriaConstructor
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.constructEmployeeOverview
import fi.evident.kriteria.test.gen.department
import fi.evident.kriteria.test.gen.name
import kotlin.test.Test

@DatabaseTest
class CriteriaConstructorQueriesTest(
    private val db: DatabaseContext,
    private val data: DefaultTestData,
) {

    @Test
    fun `simple overview using typed criteria constructor`() = transactionalTest(db) {
        val employees = em.findAll<EmployeeOverview> {
            val e = from(Employee)
            select(constructEmployeeOverview(e.name, e.department.name))
        }

        val expected = data.employees.map { EmployeeOverview(it.name, it.department.name) }
        assertThat(employees).containsExactlyElementsIn(expected)
    }

    data class EmployeeOverview @CriteriaConstructor constructor(
        val name: String,
        val departmentName: String
    )
}
