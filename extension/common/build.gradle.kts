plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                api("io.ktor:ktor-client-core:2.3.4")
                api("io.ktor:ktor-client-content-negotiation:2.3.4")
                api("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
            }
        }
        removeIf { it.name == "commonTest" }
        named("jsMain") {
            dependencies {
                api(project(":extension:chrome"))
                api(kotlin("stdlib-js"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.7.3")
                api(project.dependencies.platform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.706"))
                api("org.jetbrains.kotlin-wrappers:kotlin-web")
            }
        }
    }
}
