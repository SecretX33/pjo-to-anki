@file:OptIn(ExperimentalDistributionDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            distribution {
                commonWebpackConfig {
                    mode = when {
                        System.getenv("IS_DEVELOPMENT").toBoolean() -> KotlinWebpackConfig.Mode.DEVELOPMENT
                        else -> KotlinWebpackConfig.Mode.PRODUCTION
                    }
                }
                outputDirectory.set(file("$projectDir/../build/distributions"))
            }
        }
    }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(project(":extension:commonInterface"))
            }
        }
    }
}
