
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.dom.appendElement
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

@Suppress("UNCHECKED_CAST")
fun addCreateAnkiCardFromSentencesButtons() {
    val sentencesDivs = document.querySelectorAll("div.sentence").asList() as List<HTMLElement>
    console.info("Found '${sentencesDivs.size}' sentences found on page:", sentencesDivs)

    injectAddSentenceButtons(sentencesDivs)
    injectAddAllSentencesButton(sentencesDivs)
}

private fun injectAddSentenceButtons(sentencesDivs: List<HTMLElement>) {
    sentencesDivs.forEach { sentenceDiv ->
        val buttonsDiv = sentenceDiv.querySelector("div.port")
        if (buttonsDiv == null) {
            console.error("Didn't find buttons div!")
            return@forEach
        }
        val addAnkiCardSpan = document.createElement<HTMLElement>("span").apply {
            className = "add-anki-card"
            style.setProperty("background-image", "url('${findResourceUrl("/assets/icons/plus_icon.png")}')")

            addEventListener("click", { handleAddAnkiCardButtonClick(sentenceDiv) })
        }
        buttonsDiv.insertBefore(addAnkiCardSpan, buttonsDiv.querySelector(".expand-card"))
    }
}

/**
 *
 * Wraps the `h2` anchor, so we can inject the `Add All Sentences` button right next to it.
 *
 * We are matching the `h2` anchor by its text content, which is `Frases de Exemplo` in Portuguese, so something
 * like this:
 *
 * ```html
 * <h2>Frases de Exemplo</h2>
 * ```
 *
 * Should become something like this after the injection:
 *
 * ```html
 * <div style="margin-bottom: 25px;">
 *   <div class="add-all-anki-card-container">
 *     <h2 style="margin: 0px;">Frases de Exemplo</h2>
 *     <button class="add-all-anki-card" type="button">Adicionar todas</button>
 *   </div>
 * </div>
 * ```
 */
private fun injectAddAllSentencesButton(sentencesDivs: List<HTMLElement>) {
    val a2Anchor = document.getElementsByTagName("h2").asList()
        .filterIsInstance<HTMLElement>()
        .firstOrNull { it.textContent?.contains("Frases de Exemplo", ignoreCase = true) == true }
        ?: run {
            console.warn("Didn't find any h2 tag with 'Frases de Exemplo' to use as anchor for Add All Sentences button, skipping it for this page...")
            return
        }
    val a2Styles = window.getComputedStyle(a2Anchor)

    val addAllAnkiCardsButton = document.createElement<HTMLButtonElement>("button").apply {
        className = "add-all-anki-card"
        type = "button"
        textContent = "Adicionar todas"

        // Delegates the card creation to each individual sentence button
        addEventListener("click", {
            console.info("Adding all ${sentencesDivs.size} sentences from this page on Anki...")
            sentencesDivs.forEach(::handleAddAnkiCardButtonClick)
        })
    }

    val wrappedA2Anchor = document.createElement<HTMLElement>("div").apply {
        style.marginBottom = a2Styles.marginBottom
    }
    a2Anchor.replaceWith(wrappedA2Anchor)

    wrappedA2Anchor.appendElement("div") {
        className = "add-all-anki-card-container"

        appendChild(a2Anchor.removeMargins())
        appendChild(addAllAnkiCardsButton)
    }

    console.info("Successfully injected 'Add All Sentences' button")
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
