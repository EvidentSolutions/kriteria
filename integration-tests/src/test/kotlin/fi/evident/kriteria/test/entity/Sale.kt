package fi.evident.kriteria.test.entity

import fi.evident.kriteria.jpa.EntityMeta
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class Sale(

    @ManyToOne(fetch = LAZY)
    val employee: Employee,
    val amount: Int,
    val date: LocalDate,
) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id = 0

    companion object : EntityMeta<Sale, Int>(Sale::id)
}
