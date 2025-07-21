package fi.evident.kriteria.test.db

import fi.evident.kriteria.expression.KrExpression
import fi.evident.kriteria.expression.KrExpressionContext
import fi.evident.kriteria.jpa.findFirstOrNull
import jakarta.persistence.EntityManagerFactory

class DatabaseContext(val emf: EntityManagerFactory)

context(db: DatabaseContext)
inline fun <T> transactionally(block: context(Tx) () -> T): T {
    val session = db.emf.createEntityManager()
    try {
        val tx = session.transaction
        tx.begin()
        try {
            val result = context(Tx(session)) {
                block()
            }
            tx.commit()
            return result
        } catch (e: Exception) {
            tx.setRollbackOnly()
            throw e
        }
    } finally {
        session.close()
    }
}

context(tx: Tx)
inline fun <reified T : Any> evaluateExpression(crossinline build: context(KrExpressionContext) () -> KrExpression<out T?>): T? =
    em.findFirstOrNull<T> {
        select(build())
    }
