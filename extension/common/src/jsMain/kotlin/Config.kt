import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class ExtensionConfig(
    val version: Int,
    val ankiConnect: AnkiConnectConfig,
    val notificationConfig: NotificationConfig = NotificationConfig(),
) {
    fun trimmed(): ExtensionConfig = copy(
        ankiConnect = ankiConnect.run {
            copy(
                url = url.replace(SPACES_REGEX, ""),
                deckConfig = deckConfig.run {
                    copy(
                        name = name.trimAndNormalizeSpaces(),
                        modelName = modelName.trimAndNormalizeSpaces(),
                        frontFieldName = frontFieldName.trimAndNormalizeSpaces(),
                        backFieldName = backFieldName.trimAndNormalizeSpaces(),
                        audioFieldName = audioFieldName?.trimAndNormalizeSpaces()?.takeIf { it.isNotEmpty() },
                        tags = tags.map { it.trimAndNormalizeSpaces() }.filterTo(mutableSetOf()) { it.isNotEmpty() },
                    )
                }
            )
        }
    )
}

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
    val audioFieldName: String? = null,
    val tags: Set<String> = emptySet(),
    val checkDuplicatedsInSubdecks: Boolean,
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
