package fi.evident.kriteria.test.entity

import fi.evident.kriteria.jpa.EntityMeta
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class Department(
    val name: String,
    @ManyToOne(fetch = LAZY) val company: Company,
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id = 0

    @OneToMany(mappedBy = "department")
    val employees = mutableSetOf<Employee>()

    companion object : EntityMeta<Department, Int>(Department::id)
}
