package fi.evident.kriteria.test.db

import org.testcontainers.postgresql.PostgreSQLContainer

internal val postgresqlContainer: PostgreSQLContainer by lazy {
    val container = PostgreSQLContainer("postgres:17")
    container.start()
    container
}

