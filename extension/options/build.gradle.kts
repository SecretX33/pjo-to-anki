import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("js")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":extension:common"))
    implementation(compose.web.core)
    implementation(compose.runtime)
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            distribution(Action {
                commonWebpackConfig(Action {
                    mode = when {
                        System.getenv("IS_DEVELOPMENT").toBoolean() -> KotlinWebpackConfig.Mode.DEVELOPMENT
                        else -> KotlinWebpackConfig.Mode.PRODUCTION
                    }
                })
                outputDirectory.set(file("$projectDir/../build/distributions"))
            })
        }
    }
}
