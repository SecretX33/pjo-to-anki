package com.github.secretx33

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

/**
 * Build the `manifest.json` file, with added support for placeholders and conditional entries.
 */
class CreateBrowserManifest(
    resourceFolder: File,
    extensionFolder: File,
    rootDir: File,
) {
    private val sourceManifestFile = resourceFolder.resolve("manifest.json")
    private val destinationManifestFile = extensionFolder.resolve("manifest.json")
    private val buildGradleFile = File("$rootDir/build.gradle.kts")
    private val placeholders = mapOf(
        "name" to "PJO to Anki",
        "version" to ("version = \"((\\d+.?)+?)\"".toRegex().find(buildGradleFile.readText())?.groupValues?.get(1)
            ?: throw IllegalStateException("Didn't find 'version' field in '$buildGradleFile' file")),
        "description" to "Adiciona um botão para automatizar a criação de cartas no Anki à partir das frases do site.",
        "author" to "SecretX",
        "repository" to "https://github.com/SecretX/pjo-to-anki",
    ).mapKeys { "{{${it.key}}}" }
    private val targetBrowser: String = targetBrowser()

    /**
     * Adds support for:
     *
     * - Conditional keys, like `__firefox__some_key` or `__chrome|firefox__another_key`.
     * - Conditional values in lists, like `_firefox_http://example.com` or `__chrome|firefox__http://example.com`.
     * - Placeholders (defined in [placeholders]), like `<name>` or `<version>`.
     */
    fun create() {
        val manifestJson = readManifest()
        val manifestContents = parseManifest(manifestJson)
        val handledManifestContent = replacePlaceholders(manifestContents)
        writeManifest(handledManifestContent)
        println("Successfully generated manifest file: '$destinationManifestFile'")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> replacePlaceholders(value: T): T = when (value) {
        is String -> replacePlaceholders(value) as T
        is Iterable<*> -> replacePlaceholders(value) as T
        is Map<*, *> -> replacePlaceholders(value) as T
        else -> value
    }

    private fun replacePlaceholders(value: String): String =
        placeholders.entries.fold(value) { acc, placeholder ->
            acc.replace(placeholder.key, placeholder.value)
        }

    private fun <T> replacePlaceholders(sourceList: Iterable<T>): Iterable<T> =
        sourceList.mapNotNull { value ->
            if (!isKeyForBrowser(value)) return@mapNotNull null
            replacePlaceholders(removeBrowserPrefix(value))
        }

    private fun <K, V> replacePlaceholders(sourceMap: Map<K, V>): Map<K, V> =
        sourceMap.mapNotNull { (key, value) ->
            if (!isKeyForBrowser(key)) return@mapNotNull null
            val handledKey = replacePlaceholders(removeBrowserPrefix(key))
            val handledValue = replacePlaceholders(value)
            handledKey to handledValue
        }.toMap()

    private fun isKeyForBrowser(key: Any?): Boolean {
        if (key !is String) return true

        val match = BROWSER_PREFIX_REGEX.matchEntire(key) ?: return true
        val browsers = match.groupValues[1].split("|")
        return browsers.any { it.equals(targetBrowser, ignoreCase = true) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> removeBrowserPrefix(key: T): T {
        if (key !is String) return key
        val match = BROWSER_PREFIX_REGEX.matchEntire(key) ?: return key
        return match.groupValues[2] as T
    }

    private fun readManifest(): String = try {
        sourceManifestFile.readText()
    } catch (e: Exception) {
        throw IllegalArgumentException("Unable to read '${sourceManifestFile.absolutePath}' contents", e)
    }

    private fun parseManifest(manifestJson: String): Map<String, Any?> = try {
        JACKSON.readValue<Map<String, Any?>>(manifestJson)
    } catch (e: Exception) {
        throw IllegalArgumentException("Unable to parse '${sourceManifestFile.absolutePath}' from JSON into Map", e)
    }

    private fun writeManifest(manifestContents: Map<String, Any?>) = try {
        val manifestContentBytes = JACKSON.writerWithDefaultPrettyPrinter().writeValueAsBytes(manifestContents)
        destinationManifestFile.writeBytes(manifestContentBytes)
    } catch (e: Exception) {
        throw IllegalStateException("Unable to write parsed '${sourceManifestFile.name}' to '${destinationManifestFile.parent}'", e)
    }

    private companion object {
        val BROWSER_PREFIX_REGEX = "^__([a-z|]+)__(.+)$".toRegex(RegexOption.IGNORE_CASE)
        val JACKSON: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

}
