plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.ksp)
}

allOpen {
    // JPA plugin should add these automatically, but apparently it doesn't
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

ksp {
    arg("kriteriaProcessorTargetPackage", "fi.evident.kriteria.test.gen")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.datetime)
    implementation(libs.hibernate.core)

    kspTest(project(":symbol-processor"))

    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.postgresql.jdbc)
    testRuntimeOnly(libs.bundles.log4j)
}
