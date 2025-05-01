package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.alwaysTrue
import fi.evident.kriteria.expression.isEqualTo
import fi.evident.kriteria.jpa.delete
import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.DatabaseTest
import fi.evident.kriteria.test.db.em
import fi.evident.kriteria.test.db.transactionalTest
import fi.evident.kriteria.test.entity.Department
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.name
import kotlin.test.Test

@DatabaseTest
class DeleteTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun delete() = transactionalTest(db) {
        assertThat(em.delete(Employee) { alwaysTrue() }).isEqualTo(data.employees.size)
        assertThat(em.delete(Department) { it.name isEqualTo data.acmeRd.name }).isEqualTo(1)
    }
}
