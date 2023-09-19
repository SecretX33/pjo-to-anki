import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class ExtensionConfig(
    val version: Int,
    val ankiConnect: AnkiConnectConfig,
    val notificationConfig: NotificationConfig = NotificationConfig(),
)

@Serializable
data class NotificationConfig(
    val showOnSuccess: Boolean = true,
    val showOnWarning: Boolean = true,
    val showOnError: Boolean = true,
    val successTimeout: Duration = 1600.milliseconds,
    val warningTimeout: Duration = successTimeout,
    val errorTimeout: Duration = 4000.milliseconds,
)

@Serializable
data class AnkiConnectConfig(
    val url: String,
    val deckConfig: DeckConfig,
)

@Serializable
data class DeckConfig(
    val name: String,
    val modelName: String,
    val frontFieldName: String,
    val backFieldName: String,
    val checkDuplicatedsInSubdecks: Boolean,
)

const val CURRENT_CONFIG_VERSION: Int = 1

fun createDefaultConfig(): ExtensionConfig = ExtensionConfig(
    version = CURRENT_CONFIG_VERSION,
    ankiConnect = AnkiConnectConfig(
        url = "http://localhost:8765",
        deckConfig = DeckConfig(
            name = "Frases em Japonês",
            modelName = "Básico",
            frontFieldName = "Frente",
            backFieldName = "Verso",
            checkDuplicatedsInSubdecks = true,
        )
    ),
)
