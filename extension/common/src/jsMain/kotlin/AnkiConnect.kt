import kotlinx.serialization.Serializable

@Serializable
data class Sentence(
    val front: String,
    val back: String,
)

@Serializable
data class AnkiAddCard(
    val sentence: Sentence,
    val deck: DeckConfig,
)

suspend fun requestAnkiAddNewCard(sentence: Sentence) {
    val ankiAddCard = AnkiAddCard(
        sentence = sentence,
        deck = globalConfig().ankiConnect.deckConfig,
    )
    sendEventToBackground<Unit>(Event(EventType.ANKI_ADD_CARD, encodeJson(ankiAddCard)))
}
