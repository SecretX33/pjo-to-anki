package com.github.secretx33

fun targetBrowserOrNull(): String? = System.getenv("TARGET_BROWSER")?.lowercase()

fun targetBrowser(): String = targetBrowserOrNull()
    ?: throw IllegalStateException("Environment variable 'TARGET_BROWSER' is not set, please make sure it is set it as explained in the 'COMMANDS.md' file, then run this command again.")

fun targetBrowserFileExtension(): String = when (targetBrowser()) {
    "firefox" -> "xpi"
    else -> "zip"
}
