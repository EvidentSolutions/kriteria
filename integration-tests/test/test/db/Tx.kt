package fi.evident.kriteria.test.db

import jakarta.persistence.EntityManager

internal class Tx(val em: EntityManager)

context(tx: Tx)
internal val em: EntityManager
    get() = tx.em
