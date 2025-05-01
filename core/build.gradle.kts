plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(libs.hibernate.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.datetime)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
