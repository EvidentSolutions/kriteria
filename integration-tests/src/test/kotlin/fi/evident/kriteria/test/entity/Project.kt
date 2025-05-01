package fi.evident.kriteria.test.entity

import fi.evident.kriteria.jpa.EntityMeta
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class Project(val name: String) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id = 0

    companion object : EntityMeta<Project, Int>(Project::id)
}
