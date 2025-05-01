package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.expression.*
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.jpa.findFirstOrNull
import fi.evident.kriteria.test.db.*
import fi.evident.kriteria.test.entity.Employee
import fi.evident.kriteria.test.gen.salary
import kotlin.test.Test

@DatabaseTest
class CaseWhenQueriesTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @Test
    fun `selectCase queries`() = transactionalTest(db) {
        val salaries = em.findAll<Int> {
            val e = from(Employee)

            val funnySalary = selectCase<Int> {
                whenCase(e.salary isInRange 2000..3000, e.salary * 100)
                whenCase(e.salary isEqualTo 4000, 4001)
                otherwise(123)
            }

            select(funnySalary)
            orderBy(asc(funnySalary))
        }

        val expectedSalaries = data.employees.map {
            when (it.salary) {
                in 2000..3000 -> it.salary * 100
                4000 -> 4001
                else -> 123
            }
        }.sorted()
        assertThat(salaries).containsExactlyElementsIn(expectedSalaries).inOrder()
    }

    @Test
    fun `if then else`() = transactionalTest(db) {
        assertThat(evaluateExpression { ifThenElse(alwaysTrue(), literal(1), literal(2)) }).isEqualTo(1)
        assertThat(evaluateExpression { ifThenElse(alwaysFalse(), literal(1), literal(2)) }).isEqualTo(2)

        assertThat(evaluateExpression { ifThenElse(alwaysFalse(), 1, 2) }).isEqualTo(2)
        assertThat(evaluateExpression { ifThenElse(alwaysFalse(), literal(1), 2) }).isEqualTo(2)
        assertThat(evaluateExpression { ifThenElse(alwaysFalse(), 1, literal(2)) }).isEqualTo(2)
    }

    @Test
    fun `when case without otherwise evaluations to null if none of the patterns match `() = transactionalTest(db) {
        val result = em.findFirstOrNull<Int> {
            select(selectCase {
                whenCase(alwaysFalse(), 1)
                whenCase(alwaysFalse(), 2)
            })
        }

        assertThat(result).isNull()
    }
}

