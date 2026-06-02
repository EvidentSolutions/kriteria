package fi.evident.kriteria.jpa

import fi.evident.kriteria.expression.KrExpressionContext
import fi.evident.kriteria.expression.KrPredicate
import fi.evident.kriteria.expression.KrRoot
import fi.evident.kriteria.expression.withDefaultContexts
import fi.evident.kriteria.jpa.translation.translateDelete
import fi.evident.kriteria.jpa.translation.translateUpdate
import fi.evident.kriteria.query.KrSubQueryContext
import fi.evident.kriteria.query.KrUpdateBuilder
import fi.evident.kriteria.query.RootSetBuilder
import jakarta.persistence.EntityManager

/**
 * Executes an update in the database.
 *
 * @return the number of entities updated.
 */
@IgnorableReturnValue
public fun <T : Any> EntityManager.update(
    entityMeta: EntityMeta<T, *>,
    callback: context(KrExpressionContext, KrSubQueryContext) KrUpdateBuilder<T>.(KrRoot<T>) -> Unit,
): Int {

    val root = RootSetBuilder().newRoot(entityMeta)
    val builder = KrUpdateBuilder(root)
    withDefaultContexts {
        builder.callback(root)
    }
    val update = builder.build()

    val criteriaUpdate = translateUpdate(update, criteriaBuilder)
    return createQuery(criteriaUpdate).executeUpdate()
}

/**
 * Deletes entities matching given predicate.
 *
 * @return the number of entities deleted.
 */
@IgnorableReturnValue
public fun <T : Any> EntityManager.delete(
    entityMeta: EntityMeta<T, *>,
    predicate: context(KrExpressionContext, KrSubQueryContext) (KrRoot<T>) -> KrPredicate,
): Int {
    val root = RootSetBuilder().newRoot(entityMeta)
    val restriction = withDefaultContexts { predicate(root) }

    val criteriaDelete = translateDelete(root, restriction, criteriaBuilder)
    return createQuery(criteriaDelete).executeUpdate()
}
