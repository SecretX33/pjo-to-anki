
import chrome.runtime.ExtensionMessageEvent
import chrome.runtime.MessageSender
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.Element
import kotlin.time.Duration

val scope by lazy {
    val context = SupervisorJob()
        .plus(Dispatchers.Default)
        .plus(CoroutineName("PJO to Anki default coroutine scope"))
    CoroutineScope(context)
}

val mainScope by lazy {
    scope.plus(Dispatchers.Main)
        .plus(CoroutineName("PJO to Anki default coroutine scope"))
}

val Element.tagNameLowercase: String get() = tagName.lowercase()

fun <T : Any> jsObject(): T = js("{}") as T

fun findResourceUrl(relativePath: String): String = try {
    chrome.runtime.getURL(relativePath)
} catch (e: Throwable) {
    console.warn("Failed to get full URL of path '$relativePath', returning relative path instead", e.nonFatalOrThrow())
    relativePath
}

inline fun <reified T> encodeJson(value: T): String = try {
    Json.encodeToString<T>(value)
} catch (e: Throwable) {
    console.error("Error encoding object", value?.toNormalJsObject() ?: value, "into json", e.nonFatalOrThrow())
    throw e
}

inline fun <reified T> decodeJson(eventJson: String): T = try {
    Json.decodeFromString<T>(eventJson)
} catch (e: Throwable) {
    console.error("Error decoding json", eventJson, "\ninto ${T::class.simpleName}", e.nonFatalOrThrow())
    throw e
}

inline fun <reified T : Any> T.toNormalJsObject(
    encodeDefault: Boolean = true,
): Any = try {
    val kotlinJson = Json {
        this.encodeDefaults = encodeDefault
    }
    JSON.parse(kotlinJson.encodeToString<T>(this))
} catch (e: Throwable) {
    e.nonFatalOrThrow()
    this
}

inline fun ExtensionMessageEvent.addMessageListener(crossinline callback: suspend (message: String, sender: MessageSender) -> Unit) {
    addListener { request, sender, sendResponse ->
        console.debug("Received message:", request)
        if (request !is String) {
            console.warn("Received message not encoded, silently ignoring. Type: '${request::class.simpleName}'. Message:", request)
            return@addListener
        }
        scope.launch {
            callback(request, sender)
        }
        sendResponse(Unit)
    }
}

fun NonFatal(t: Throwable): Boolean =
    when (t) {
        is CancellationException -> false
        else -> true
    }

fun Throwable.nonFatalOrThrow(): Throwable =
    if (NonFatal(this)) this else throw this

suspend inline fun <T> suspendCancellableCoroutine(
    timeout: Duration,
    crossinline block: (CancellableContinuation<T>) -> Unit,
): T = withTimeout(timeout) {
    suspendCancellableCoroutine(block)
}
