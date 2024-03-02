import chrome.runtime.MessageSender
import web.console.console

fun startNotificationEventListener() = chrome.runtime.onMessage.addMessageListener { eventJson, sender ->
    onEventReceived(eventJson, sender)
}

private val eventHandlers: EventHandlerMap = mapOf(
    EventType.NOTIFICATION to {
        val requestNotification = decodeJson<RequestNotification>(it)
        showNotification(
            options = requestNotification.options,
            extensionConfig = requestNotification.config,
        )
    }
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
