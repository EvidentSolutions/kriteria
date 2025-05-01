package fi.evident.kriteria.test.db

import jakarta.persistence.EntityManager

class Tx(val em: EntityManager)

context(tx: Tx)
val em: EntityManager
    get() = tx.em
