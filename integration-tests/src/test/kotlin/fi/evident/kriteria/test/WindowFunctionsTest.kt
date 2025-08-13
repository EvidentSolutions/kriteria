package fi.evident.kriteria.test

import com.google.common.truth.Truth.assertThat
import fi.evident.kriteria.annotations.CriteriaConstructor
import fi.evident.kriteria.expression.asc
import fi.evident.kriteria.expression.avg
import fi.evident.kriteria.expression.sum
import fi.evident.kriteria.jpa.findAll
import fi.evident.kriteria.test.db.*
import fi.evident.kriteria.test.entity.Sale
import fi.evident.kriteria.test.gen.*
import org.junit.jupiter.api.BeforeAll
import java.time.LocalDate
import kotlin.test.Test

@DatabaseTest
class WindowFunctionsTest(private val db: DatabaseContext, private val data: DefaultTestData) {

    @BeforeAll
    fun `initialize sales data`() = transactionalTest(db) {
        truncateTable(Sale)

        persistAll(
            listOf(
                Sale(data.barry, 1000, LocalDate.of(2024, 1, 15)),
                Sale(data.belle, 1500, LocalDate.of(2024, 1, 20)),
                Sale(data.john, 800, LocalDate.of(2024, 1, 18)),
                Sale(data.barry, 1200, LocalDate.of(2024, 2, 10)),
                Sale(data.sarah, 2000, LocalDate.of(2024, 2, 5)),
                Sale(data.belle, 900, LocalDate.of(2024, 2, 15)),
                Sale(data.john, 1100, LocalDate.of(2024, 2, 20)),
            )
        )
    }

    @Test
    fun `results from native`() = transactionalTest(db) {
        val rows = em.findAll<QueryResultRow> {
            val sale = from(Sale)
            val employee = innerJoin(sale.employee)
            val department = innerJoin(employee.department)

            val employeeWindow = window {
                partitionBy(employee.name)
                orderBy(asc(sale.date))
            }

            val departmentWindow = window {
                partitionBy(department.name)
            }

            select(
                constructQueryResultRow(
                    employee.name,
                    department.name,
                    sale.amount,
                    sale.date,
                    employeeWindow.sum(sale.amount),
                    departmentWindow.avg(sale.amount),
                )
            )

            orderBy(asc(department.name) then asc(employee.name) then asc(sale.date))
        }

        assertThat(rows).containsExactly(
            QueryResultRow("Barry Bar", "R&D", 1000, LocalDate.of(2024, 1, 15), 1000, 1150.0),
            QueryResultRow("Barry Bar", "R&D", 1200, LocalDate.of(2024, 2, 10), 2200, 1150.0),
            QueryResultRow("Belle Baz", "R&D", 1500, LocalDate.of(2024, 1, 20), 1500, 1150.0),
            QueryResultRow("Belle Baz", "R&D", 900, LocalDate.of(2024, 2, 15), 2400, 1150.0),
            QueryResultRow("John Connor", "Sales", 800, LocalDate.of(2024, 1, 18), 800, 1300.0),
            QueryResultRow("John Connor", "Sales", 1100, LocalDate.of(2024, 2, 20), 1900, 1300.0),
            QueryResultRow("Sarah Connor", "Sales", 2000, LocalDate.of(2024, 2, 5), 2000, 1300.0),
        ).inOrder()
    }

    data class QueryResultRow @CriteriaConstructor constructor(
        val employeeName: String,
        val departmentName: String,
        val saleAmount: Int,
        val saleDate: LocalDate,
        val runningTotal: Long,
        val deptAvg: Double,
    )
}

