package fi.evident.kriteria.test.entity

import fi.evident.kriteria.jpa.EntityMeta
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class Employee(
    val name: String,
    val salary: Int,
    @ManyToOne(fetch = LAZY) val department: Department,
    @ManyToOne(fetch = LAZY) val manager: Employee? = null,
) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id = 0

    @ManyToMany
    val projectRoles = mutableMapOf<Project, ProjectRole>()

    @Suppress("unused") // for now, this is only present to check that processor doesn't choke on it
    @ManyToMany
    val friends = setOf<Employee>()

    @Suppress("unused") // for now, this is only present to check that processor doesn't choke on it
    @OneToMany
    val projectsByName = mutableMapOf<String, Project>()

    companion object : EntityMeta<Employee, Int>(Employee::id)
}
