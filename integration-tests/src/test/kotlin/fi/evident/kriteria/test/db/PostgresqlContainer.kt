package fi.evident.kriteria.test.db

import org.testcontainers.containers.PostgreSQLContainer

val postgresqlContainer: PostgreSQLContainer<*> by lazy {
    val container = PostgreSQLContainer("postgres:17")
    container.start()
    container
}

