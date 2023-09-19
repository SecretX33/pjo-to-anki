import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import kotlinx.serialization.Serializable

@Serializable
data class Note(val note: NoteOptions)

@Serializable
data class NoteOptions(
    val deckName: String,
    val modelName: String,
    val fields: Map<String, String>,
    val options: NoteInnerOptions,
    val tags: Set<String> = emptySet(),
)

@Serializable
class NoteInnerOptions(
    val allowDuplicate: Boolean,
    val duplicateScope: String,
    val duplicateScopeOptions: DuplicateNoteOptions,
)

@Serializable
data class DuplicateNoteOptions(
    val deckName: String,
    val checkChildren: Boolean,
    val checkAllModels: Boolean,
)

fun handleAnkiAddNewCardEvent(): EventHandler = { json ->
    val ankiAddCard = decodeJson<AnkiAddCard>(json)
    val sentence = ankiAddCard.sentence
    val deck = ankiAddCard.deck

    val actionName = "addNote"
    val params = buildNote(sentence, deck)

    try {
        val response = doAnkiRequest<Note, Unit>(actionName, params)
        console.info("Card created on Anki via AnkiConnect. Response:", response)
        notifySuccess("A carta foi criada com sucesso.", title = "Carta adicionada")
    } catch (e: Throwable) {
        val errorMessage = e.nonFatalOrThrow().message ?: e.toString()
        val isDuplicateCardError = errorMessage.contains("cannot create note because it is a duplicate", ignoreCase = true)
        if (isDuplicateCardError) {
            console.warn("Card already exists on deck, no action was taken.")
            notifyWarning("Essa carta já faz parte do seu baralho.", title = "Carta duplicada")
        } else {
            // General error
            console.warn("Request to Anki failed!", e)
            notifyError("Não foi possível criar a carta no Anki.\n\nDetalhes: ${e.message ?: e.toString()}", title = "Erro")
        }
    }
}

fun buildNote(sentence: Sentence, deck: DeckConfig): Note = Note(NoteOptions(
    deckName = deck.name,
    modelName = deck.modelName,
    fields = mapOf(
        deck.frontFieldName to sentence.front,
        deck.backFieldName to sentence.back,
    ),
    options = NoteInnerOptions(
        allowDuplicate = false,
        duplicateScope = "deck",
        duplicateScopeOptions = DuplicateNoteOptions(
            deckName = deck.name,
            checkChildren = deck.checkDuplicatedsInSubdecks,
            checkAllModels = false,
        ),
    )
))

@Serializable
data class AnkiBodyRequest<T>(
    val action: String,
    val version: Int,
    val params: T,
)

const val ANKI_API_VERSION = 6

suspend inline fun <reified T, reified R> doAnkiRequest(
    actionName: String,
    param: T,
    isJson: Boolean = true,
    version: Int = ANKI_API_VERSION,
): R {
    val body = encodeJson<AnkiBodyRequest<T>>(AnkiBodyRequest(action = actionName, version = version, params = param))
    val url = globalConfig().ankiConnect.url

    console.info("Requesting to '$url' with body:", body.toNormalJsObject())

    try {
        val response = httpClient.post(url) {
            headers.apply {
                if (isJson) this["Content-Type"] = ContentType.Application.Json.toString()
            }
            setBody(body)
        }

        val data = JSON.parse<Any>(response.bodyAsText())
        console.info("Parsed '$url' response", data, "\nClass: ${data::class.simpleName}")
        data.asDynamic().error.unsafeCast<String?>()?.let { throw AnkiException(it) }

        return when {
            Unit is R -> Unit
            else -> data.asDynamic().result as R
        }
    } catch (e: Throwable) {
        throw when (e.nonFatalOrThrow()) {
            is AnkiException -> e
            else -> Exception("Request to '$url' failed", e)
        }
    }
}

class AnkiException(message: String, cause: Throwable? = null) : Exception(message = message, cause = cause)

