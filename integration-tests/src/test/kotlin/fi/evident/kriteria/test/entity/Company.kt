package fi.evident.kriteria.test.entity

import fi.evident.kriteria.jpa.EntityMeta
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Company(
    val name: String
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id = 0

    @OneToMany(mappedBy = "company")
    val departments = mutableSetOf<Department>()

    companion object : EntityMeta<Company, Int>(Company::id)
}
