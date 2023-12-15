import com.github.secretx33.CreateBrowserManifest
import com.github.secretx33.FixWebpackModuleDefinition
import com.github.secretx33.targetBrowser
import com.github.secretx33.targetBrowserFileExtension
import com.github.secretx33.targetBrowserOrNull
import kotlin.io.path.moveTo

plugins {
    kotlin("js") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21" apply false
    id("org.jetbrains.compose") version "1.5.11" apply false
}

dependencies {
    implementation(project(":extension:background"))
    implementation(project(":extension:contentScript"))
    implementation(project(":extension:popup"))
}

kotlin {
    js(IR) {
        browser()
    }
}

tasks {
    // Copy js scripts
    val background = ":extension:background:browserDistribution"
    val content = ":extension:contentScript:browserDistribution"
    val popup = ":extension:popup:browserDistribution"
    val options = ":extension:options:browserDistribution"
    val extensionFolder = file("$projectDir/build/extension")

    val copyBundleFile by registering(Copy::class) {
        dependsOn(background, content, popup, options)
        from("${layout.buildDirectory.asFile.get()}/distributions") {
            include("**/*.js")
        }
        into("$extensionFolder/js")
    }

    // Copy resources
    val copyResources by registering(Copy::class) {
        outputs.upToDateWhen { false }
        val resourceFolder = file("$projectDir/src/main/resources")

        from("$resourceFolder/css") { into("css") }
        from("$resourceFolder/icons") { into("assets/icons") }
        from("$resourceFolder/html")
        from("$resourceFolder/js") { into("js") }
        into(extensionFolder)

        doLast {
            CreateBrowserManifest(resourceFolder, extensionFolder, rootDir).create()
        }
    }

    // Build modules
    val buildExtension by registering {
        group = "build"
        dependsOn(copyBundleFile, copyResources)
        doFirst {
            println("Building extension for '${targetBrowserOrNull()}'")
        }
        doLast {
            FixWebpackModuleDefinition(extensionFolder.resolve("js")).fix()
        }
    }

    // Zip extension
    val packageExtension by registering(Zip::class) {
        group = "build"
        dependsOn(buildExtension)
        from(extensionFolder)
        doLast {
            val packagedExtension = archiveFile.get().asFile.toPath()
            val targetPackageExtension = packagedExtension.parent.resolve("${targetBrowser()}.${targetBrowserFileExtension()}")
            packagedExtension.moveTo(targetPackageExtension, overwrite = true)
        }
    }
}
