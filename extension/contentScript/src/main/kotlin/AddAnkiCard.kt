import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.css.CSSStyleDeclaration

@Suppress("UNCHECKED_CAST")
fun injectCreateAnkiCardFromSentencesButtons() {
    val sentencesDivs = document.querySelectorAll("div.sentence").asList() as List<HTMLElement>
    console.info("Found '${sentencesDivs.size}' sentences found on page:", sentencesDivs)

    sentencesDivs.forEach { sentenceDiv ->
        val buttonsDiv = sentenceDiv.querySelector("div.port")
        if (buttonsDiv == null) {
            console.error("Didn't find buttons div!")
            return
        }
        val addAnkiCardSpan = document.createElement("span")

        val cssProperties = mapOf(
            "background-image" to "url('${findResourceUrl("/assets/icons/plus_icon.png")}')",
            "background-size" to "100% 100%",
            "margin-left" to "0.8rem",
            "display" to "inline-table",
            "height" to "0.8rem",
            "width" to "0.8rem",
            "cursor" to "pointer",
        )

        cssProperties.forEach { (key, value) ->
            addAnkiCardSpan.asDynamic().style.unsafeCast<CSSStyleDeclaration>().setProperty(key, value)
        }

        buttonsDiv.insertBefore(addAnkiCardSpan, buttonsDiv.querySelector(".expand-card"))
        addAnkiCardSpan.addEventListener("click", { handleAddAnkiCardButtonClick(sentenceDiv) })
    }
}

private fun handleAddAnkiCardButtonClick(sentenceDiv: Element) {
    val cardDiv = sentenceDiv.querySelector(".card")
        ?: throw IllegalStateException("Could not find 'card' in sentence div")
    val sentence = extractSentenceFromCard(cardDiv)
    scope.launch {
        try {
            requestAnkiAddNewCard(sentence)
        } catch (e: Throwable) {
            console.error("Failed to send ${EventType.ANKI_ADD_CARD} event", e.nonFatalOrThrow())
        }
    }
}

private fun extractSentenceFromCard(cardDiv: Element): Sentence {
    val front = (cardDiv.querySelector(".card-front") as? HTMLElement)?.innerHTML
        ?: throw IllegalStateException("Could not find sentence of the front of the card")
    val back = (cardDiv.querySelector(".card-back") as? HTMLElement)?.innerHTML
        ?: throw IllegalStateException("Could not find sentence of the back of the card")

    return Sentence(
        front = front,
        back = back,
    )
}
