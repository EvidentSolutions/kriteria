plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(21)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.dokka.gradlePlugin)
    implementation(libs.vanniktechMavenPublish.gradlePlugin)
}
