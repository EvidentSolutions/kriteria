package fi.evident.kriteria.test.db

import fi.evident.kriteria.jpa.EntityMeta
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException

fun <E : Any, I : Any> EntityManager.findRequired(type: EntityMeta<E, I>, id: I): E =
    find(type.entityClass.java, id) ?: throw EntityNotFoundException("$type, id=$id")

context(tx: Tx)
fun persistAll(entities: Iterable<Any>) {
    for (entity in entities)
        tx.em.persist(entity)
}

