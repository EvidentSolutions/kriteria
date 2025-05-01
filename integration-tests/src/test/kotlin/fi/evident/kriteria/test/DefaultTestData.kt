package fi.evident.kriteria.test

import fi.evident.kriteria.test.db.DatabaseContext
import fi.evident.kriteria.test.db.persistAll
import fi.evident.kriteria.test.db.transactionally
import fi.evident.kriteria.test.db.truncateTables
import fi.evident.kriteria.test.entity.*

class DefaultTestData private constructor() {
    val acme = Company(name = "ACME Industries")
    val acmeHr = Department(name = "HR", company = acme)
    val acmeRd = Department(name = "R&D", company = acme)
    val fred = Employee(name = "Fred Foo", department = acmeHr, salary = 3000)
    val barry = Employee(name = "Barry Bar", department = acmeRd, salary = 3500)
    val belle = Employee(name = "Belle Baz", department = acmeRd, salary = 4000, manager = barry)

    val cyberdyne = Company(name = "Cyberdyne Systems")
    val cyberdyneSales = Department(name = "Sales", company = cyberdyne)
    val cyberdyneAlignment = Department(name = "Alignment", company = cyberdyne)
    val john = Employee(name = "John Connor", department = cyberdyneSales, salary = 5000)
    val sarah = Employee(name = "Sarah Connor", department = cyberdyneSales, salary = 6000)

    val skynet = Project("Skynet")

    val salesManager = ProjectRole("Sales Manager")

    val companies = listOf(acme, cyberdyne)
    val departments = listOf(acmeHr, acmeRd, cyberdyneSales, cyberdyneAlignment)
    val employees = listOf(fred, barry, belle, john, sarah)
    val projects = listOf(skynet)
    val projectRoles = listOf(salesManager)

    context(db: DatabaseContext)
    private fun prepare() {
        transactionally {
            truncateTables()

            persistAll(companies)
            persistAll(departments)
            persistAll(projects)
            persistAll(projectRoles)

            john.projectRoles[skynet] = salesManager

            persistAll(employees)

            for (employee in employees)
                employee.department.employees += employee
        }
    }

    companion object {
        fun applyTo(db: DatabaseContext): DefaultTestData {
            val data = DefaultTestData()
            context(db) {
                data.prepare()
            }
            return data
        }
    }
}
