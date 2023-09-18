package com.github.secretx33

import java.io.File
import java.nio.file.StandardOpenOption
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.writeText

class FixWebpackModuleDefinition(private val jsFolder: File) {

    /**
     * In some cases, the `root` can be null (like when loading a JS file background script), which breaks
     * webpack loading.
     *
     * These replacements add some null checks before assigning return of the webpack factory to a variable,
     * which fix this issue.
     */
    fun fix() {
        jsFolder.toPath().listDirectoryEntries().forEach {
            val replacements = getReplacements(it.nameWithoutExtension)
            val replacedContent = replacements.entries.fold(it.readText()) { acc, replacement ->
                acc.replaceFirst(replacement.key, replacement.value)
            }
            it.writeText(replacedContent, options = arrayOf(StandardOpenOption.TRUNCATE_EXISTING))
            println("Applied webpack module fix on file: $it")
        }
    }

    private fun getReplacements(nameWithoutExtension: String): Map<Regex, String> = mapOf(
        """else[\s\n]+root\["$nameWithoutExtension"\] = factory\(\);""".toRegex() to """
            else if (root != null)
                    root["$nameWithoutExtension"] = factory();
                else {
                    factory();
                }
        """.trimIndent(),
        """\?(exports\.$nameWithoutExtension=\w+\(\)):((\w+)\.$nameWithoutExtension=(\w+\(\)))""".toRegex() to "?$1:$3!=null?$2:$4",
    )
}
