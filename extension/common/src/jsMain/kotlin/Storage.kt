
import chrome.storage.LocalStorageArea
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

private const val GLOBAL_CONFIG_KEY = "globalConfig"

private val localStorage: LocalStorageArea
    get() = chrome.storage.local

suspend fun globalConfig(): ExtensionConfig {
    val config = localStorage.getSuspend<Any?>(GLOBAL_CONFIG_KEY)
        ?.let { runCatching { decodeJson<ExtensionConfig>(JSON.stringify(it)) }.getOrNull() }
        ?.also { console.info("Found existing config", it.toNormalJsObject()) }
        ?: createDefaultConfig().also {
            console.info("New config created", it.toNormalJsObject())
            setGlobalConfig(it)
        }
    return config
}

suspend fun setGlobalConfig(config: ExtensionConfig) {
    val map = jsObject<dynamic>()
    map[GLOBAL_CONFIG_KEY] = JSON.parse(encodeJson(config))
    localStorage.setSuspend(map as Any)
}

@Suppress("UNCHECKED_CAST")
suspend fun <T> LocalStorageArea.getSuspend(key: String): T = suspendCancellableCoroutine(timeout = 5.seconds) { continuation ->
    try {
        get(key) {
            continuation.resume(it[key] as T)
        }
    } catch (e: Throwable) {
        continuation.resumeWithException(e)
    }
}

suspend fun LocalStorageArea.setSuspend(value: Any) = suspendCancellableCoroutine(timeout = 5.seconds) { continuation ->
    try {
        set(value) {
            continuation.resume(Unit)
        }
    } catch (e: Throwable) {
        continuation.resumeWithException(e)
    }
}
