import chrome.runtime.MessageSender
import web.console.console

fun main() {
    chrome.runtime.onInstalled.addListener {
        console.info("「 PJO to Anki (Backend) 」")
    }
    chrome.runtime.onMessage.addMessageListener { message, sender ->
        onEventReceived(message, sender)
    }
}

private val eventHandlers: EventHandlerMap = mapOf(
    EventType.ANKI_ADD_CARD to handleAnkiAddNewCardEvent()
)

private suspend fun onEventReceived(eventJson: String, sender: MessageSender) {
    val event = decodeJson<Event>(eventJson)
    val handler = eventHandlers[event.type]
    console.debug("Event has been received:", event.toNormalJsObject(), "\nAnd sender is:", sender, "\nAnd handler is:", handler)
    handler?.let {
        try {
            it(event.content)
        } catch (e: Throwable) {
            console.error("An unexpected exception occurred while handling event '${event.type}'", e.nonFatalOrThrow())
        }
    }
}
