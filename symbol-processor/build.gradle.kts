plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.maven-publish")
}

description = "Symbol processor to generate typed for Kriteria metamodel from JPA classes"

dependencies {
    implementation(project(":core"))
    implementation(libs.hibernate.core)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.ksp.api)
}
