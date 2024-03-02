import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class RequestNotification(
    val options: NotificationOptions,
    val config: ExtensionConfig,
)

private const val DEFAULT_TITLE = "PJO to Anki"

@Serializable
data class NotificationOptions(
    val type: NotificationType,
    var title: String? = DEFAULT_TITLE,
    var message: String? = null,
    var icon: String? = null,
    val timeout: Duration? = null,
    val useEvent: Boolean = true,
)

enum class NotificationType {
    SUCCESS,
    INFO,
    WARNING,
    ERROR;

    val jsName = name.lowercase()
}

enum class PositionClass(val jsName: String) {
    TOP_LEFT("topLeft"),
    BOTTOM_LEFT("bottomLeft"),
    BOTTOM_RIGHT("bottomRight"),
    TOP_RIGHT("topRight"),
    TOP_CENTER("topCenter"),
    BOTTOM_CENTER("bottomCenter"),
}

fun notifySuccess(
    message: String,
    title: String? = null,
    timeout: Duration? = null,
    useEvent: Boolean = true,
) {
    val options = NotificationOptions(
        title = title ?: DEFAULT_TITLE,
        message = message,
        type = NotificationType.SUCCESS,
        timeout = timeout,
        useEvent = useEvent,
    )
    notify(options)
}

fun notifyWarning(
    message: String,
    title: String? = null,
    timeout: Duration? = null,
    useEvent: Boolean = true,
) {
    val options = NotificationOptions(
        title = title ?: DEFAULT_TITLE,
        message = message,
        type = NotificationType.WARNING,
        timeout = timeout,
        useEvent = useEvent,
    )
    notify(options)
}

fun notifyError(
    message: String,
    title: String? = null,
    timeout: Duration? = null,
    useEvent: Boolean = true,
) {
    val options = NotificationOptions(
        title = title ?: DEFAULT_TITLE,
        message = message,
        type = NotificationType.ERROR,
        timeout = timeout,
        useEvent = useEvent,
    )
    notify(options)
}

private fun notify(options: NotificationOptions) {
    scope.launch {
        if (!options.useEvent) {
            showNotification(options)
            return@launch
        }
        val requestNotification = RequestNotification(
            options = options,
            config = globalConfig(),
        )
        val event = Event(EventType.NOTIFICATION, encodeJson(requestNotification))
        sendEventToContentScript<Unit>(event)
    }
}

@Suppress("NAME_SHADOWING")
suspend fun showNotification(options: NotificationOptions, extensionConfig: ExtensionConfig? = null) {
    val notificationType = options.type
    val config = (extensionConfig ?: globalConfig()).notificationConfig
    val options = options.copy(
        timeout = options.timeout ?: when (notificationType) {
            NotificationType.SUCCESS -> config.successTimeout
            NotificationType.WARNING -> config.warningTimeout
            NotificationType.ERROR -> config.errorTimeout
            else -> options.timeout
        }
    )

    if (notificationType == NotificationType.SUCCESS && !config.showOnSuccess
        || notificationType == NotificationType.WARNING && !config.showOnWarning
        || notificationType == NotificationType.ERROR && !config.showOnError) {
        // Notification is disabled for the given notificationType
        return
    }

    mainScope.launch {
        console.debug("Displaying notification:", options.toNormalJsObject())
        dispatchNotificationToLibrary(options)
    }
}

private fun dispatchNotificationToLibrary(options: NotificationOptions): Toast = VanillaToasts.create(options.toVanillaToastOptions())

private external val VanillaToasts: VanillaToast

external class Toast {
    val id: String
    fun hide()
}

private external interface VanillaToast {
    fun create(options: VanillaToastOptions): Toast
}

private external interface VanillaToastOptions {
    var title: String? get() = definedExternally; set(value) = definedExternally
    var text: String? get() = definedExternally; set(value) = definedExternally
    var icon: String? get() = definedExternally; set(value) = definedExternally
    var timeout: Int? get() = definedExternally; set(value) = definedExternally
    /**
     * See: [NotificationType]
     */
    var type: String? get() = definedExternally; set(value) = definedExternally
    /**
     * See: [PositionClass]
     */
    var positionClass: String? get() = definedExternally; set(value) = definedExternally
    var callback: Function<*>? get() = definedExternally; set(value) = definedExternally
}

private fun VanillaToastOptions(
    title: String? = null,
    message: String? = null,
    timeout: Int? = null,
    positionClass: PositionClass? = null,
    type: NotificationType? = null,
): VanillaToastOptions = jsObject<VanillaToastOptions>().also {
    it.title = title
    it.text = message
    it.timeout = timeout
    it.positionClass = positionClass?.jsName
    it.type = type?.jsName
}

private fun NotificationOptions.toVanillaToastOptions(): VanillaToastOptions = VanillaToastOptions(
    title = title,
    message = message,
    timeout = timeout?.inWholeMilliseconds?.toInt(),
    positionClass = PositionClass.TOP_RIGHT,
    type = type,
)
