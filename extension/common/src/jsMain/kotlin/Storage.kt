
import chrome.storage.LocalStorageArea
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Json
import kotlin.time.Duration.Companion.seconds

private const val GLOBAL_CONFIG_KEY = "globalConfig"

private val localStorage: LocalStorageArea
    get() = chrome.storage.local

suspend fun globalConfig(): ExtensionConfig {
    val config = localStorage.getSuspend(GLOBAL_CONFIG_KEY)[GLOBAL_CONFIG_KEY]
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

suspend fun LocalStorageArea.getSuspend(key: String): Json = suspendCancellableCoroutineWithTimeout(5.seconds) { continuation ->
    try {
        get(key) {
            continuation.resume(it)
        }
    } catch (e: Throwable) {
        continuation.resumeWithException(e)
    }
}

suspend fun LocalStorageArea.setSuspend(value: Any) = suspendCancellableCoroutineWithTimeout(5.seconds) { continuation ->
    try {
        set(value) {
            continuation.resume(Unit)
        }
    } catch (e: Throwable) {
        continuation.resumeWithException(e)
    }
}
