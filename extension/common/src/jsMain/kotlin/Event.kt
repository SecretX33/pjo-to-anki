import chrome.tabs.QueryInfo
import kotlinx.serialization.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

typealias EventHandler = suspend (String) -> Unit
typealias EventHandlerMap = Map<EventType, EventHandler>

@Serializable
data class Event(
    val type: EventType,
    val content: String,
)

@Serializable
enum class EventType {
    NOTIFICATION,
    ANKI_ADD_CARD,
}

suspend inline fun <reified R> sendEventToBackground(event: Event): R = suspendCancellableCoroutineWithTimeout(10.seconds) { continuation ->
    console.info("Sending event to background", event.toNormalJsObject())
    try {
        chrome.runtime.sendMessage(encodeJson<Event>(event)) {
            when {
                Unit is R -> continuation.resume(Unit)
                it !is R -> continuation.resumeWithException(IllegalArgumentException("Invalid response: received $it as response, but was expecting ${R::class.simpleName} instead"))
                else -> continuation.resume(it as R)
            }
        }
    } catch (e: Throwable) {
        console.error("Could not send event", event, e)
        continuation.resumeWithException(e)
    }
}

suspend inline fun <reified R> sendEventToContentScript(event: Event): R {
    console.info("Sending event to content script", event.toNormalJsObject())

    val activeTabs = getActiveTabs().ifEmpty {
        throw IllegalArgumentException("Get active tabs returned no tab, so there's no way to send event ${event.toNormalJsObject()} to content script")
    }
    val firstTabId = activeTabs[0].id ?: throw IllegalArgumentException("First tab has no assigned ID. Tabs: $activeTabs")

    return suspendCancellableCoroutineWithTimeout(5.seconds) { continuation ->
        try {
            chrome.tabs.sendMessage(firstTabId, encodeJson<Event>(event)) {
                when {
                    Unit is R -> continuation.resume(Unit)
                    it !is R -> continuation.resumeWithException(IllegalArgumentException("Invalid response: received $it as response, but was expecting ${R::class.simpleName} instead"))
                    else -> continuation.resume(it as R)
                }
            }
        } catch (e: Throwable) {
            console.error("Could not send event", event, e)
            continuation.resumeWithException(e)
        }
    }
}

suspend fun getActiveTabs() = suspendCancellableCoroutineWithTimeout(5.seconds) { continuation ->
    try {
        val queryInfo = jsObject<QueryInfo>().apply {
            active = true
            currentWindow = true
        }
        chrome.tabs.query(queryInfo) { continuation.resume(it) }
    } catch (e: Throwable) {
        console.error("Could not recover currently active tabs", e)
        continuation.resumeWithException(e)
    }
}
