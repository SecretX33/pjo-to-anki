@file:OptIn(ExperimentalDistributionDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":extension:common"))
            }
        }

        named("jsMain") {
            dependencies {
                api(project.dependencies.platform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.706"))
                api("org.jetbrains.kotlin-wrappers:kotlin-emotion")
                api("org.jetbrains.kotlin-wrappers:kotlin-react")
                api("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                api("org.jetbrains.kotlin-wrappers:kotlin-mui-material")
            }
        }
    }
}
