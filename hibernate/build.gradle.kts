plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    api(project(":core"))
    api(libs.hibernate.core)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
