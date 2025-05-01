plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.maven-publish")
}

description = "A type-safe Kotlin Criteria builder for Hibernate"

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.datetime)
    implementation(libs.hibernate.core)

    testImplementation(libs.bundles.testing)
}
