package fi.evident.kriteria.test.db

import fi.evident.kriteria.test.gen.generatedEntityMetas
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.JdbcSettings.*
import org.hibernate.cfg.SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION
import org.hibernate.dialect.PostgreSQLDialect

inline fun transactionalTest(db: DatabaseContext, block: context(Tx) () -> Unit) {
    testWithDatabaseContext(db) {
        transactionally {
            block()
        }
    }
}

inline fun testWithDatabaseContext(db: DatabaseContext, block: context(DatabaseContext) () -> Unit) {
    context(db) {
        block()
    }
}

context(tx: Tx)
fun truncateTables() {
    // We make the assumption that naming strategy maps class names directly to table names.
    // If the assumption is incorrect, we'll find out immediately as the tests fail.
    val sql = generatedEntityMetas.joinToString(prefix = "truncate table ", separator = ", ", postfix = " cascade") {
        it.entityClass.simpleName.toString()
    }
    @Suppress("SqlSourceToSinkFlow")
    tx.em.createNativeQuery(sql).executeUpdate()
}

fun buildSessionFactory(): SessionFactory {
    val configuration = Configuration()

    configuration.setProperty(JAKARTA_JDBC_DRIVER, "org.postgresql.Driver")
    configuration.setProperty(JAKARTA_JDBC_URL, postgresqlContainer.jdbcUrl)
    configuration.setProperty(JAKARTA_JDBC_USER, postgresqlContainer.username)
    configuration.setProperty(JAKARTA_JDBC_PASSWORD, postgresqlContainer.password)
    configuration.setProperty(JAKARTA_HBM2DDL_DATABASE_ACTION, "create-drop")
    configuration.setProperty(DIALECT, PostgreSQLDialect::class.qualifiedName)
    configuration.setProperty(SHOW_SQL, "true")
    configuration.setProperty(FORMAT_SQL, "true")

    for (type in generatedEntityMetas)
        configuration.addAnnotatedClass(type.entityClass.java)

    return configuration.buildSessionFactory()
}
