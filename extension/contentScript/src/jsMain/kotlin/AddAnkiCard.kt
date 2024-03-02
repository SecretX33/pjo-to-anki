import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.dom.appendElement
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import web.console.console

@Suppress("UNCHECKED_CAST")
fun addCreateAnkiCardFromSentencesButtons() {
    val sentencesDivs = document.querySelectorAll(SENTENCE_DIV_CSS_SELECTOR).asList() as List<HTMLElement>
    console.debug("Found '${sentencesDivs.size}' sentences found on page:", sentencesDivs)

    injectAddSentenceButtons(sentencesDivs)
    injectAddAllSentencesButtons(sentencesDivs)
}

private fun injectAddSentenceButtons(sentencesDivs: List<HTMLElement>) {
    sentencesDivs.forEach { sentenceDiv ->
        val buttonsDiv = sentenceDiv.querySelector(SENTENCE_BUTTONS_CSS_SELECTOR)
        if (buttonsDiv == null) {
            console.error("Didn't find buttons div!")
            return@forEach
        }
        val addAnkiCardButton = addCardButton(sentenceDiv)
        val expandCardDetailsButton = buttonsDiv.querySelector(".expand-card")
        buttonsDiv.insertBefore(addAnkiCardButton, expandCardDetailsButton)
    }
}

private fun addCardButton(sentenceDiv: HTMLElement): HTMLElement =
    document.createElement<HTMLElement>("span").apply {
        className = "add-anki-card"
        style.setProperty("background-image", "url('${findResourceUrl("/assets/icons/plus_icon.png")}')")

        addEventListener("click", { handleAddAnkiCardButtonClick(sentenceDiv) })
    }

/**
 * Find viable points where it would make sense to inject the `Add All Sentences`, and add these buttons into
 * the page.
 *
 * The goal is to find and group sentences into blocks, find a viable anchor if it exists (or create one if it
 * doesn't), then add the 'Add All Sentences' button right next to it. An example of a viable anchor is a title,
 * such as:
 *
 * ```html
 * <h2>Frases de Exemplo</h2>
 * ```
 *
 * That will be transformed into something like this:
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
private fun injectAddAllSentencesButtons(sentencesDivs: List<HTMLElement>) {
    val injectPoints = findAddAllSentencesInjectPoints(sentencesDivs).ifEmpty {
        console.warn("No inject points found, skipping 'Add All Sentences' button for this page...")
        return
    }
    console.debug("Found ${injectPoints.size} inject points for 'Add All Sentences' buttons:", injectPoints.toTypedArray())

    injectPoints.forEachIndexed { index, injectPoint ->
        val anchor = injectPoint.anchor
        val sentences = injectPoint.sentences.toList()

        val anchorElement = when (anchor) {
            is AnchorPoint.Missing -> document.createElement("h2").apply {
                textContent = DEFAULT_ANCHOR_TEXT
                val firstSentence = sentences.first()
                firstSentence.parentElement!!.insertBefore(this, firstSentence)
            }
            is AnchorPoint.Title -> anchor.titleElement
        }
        val addAllAnkiCardsButton = addAllAnkiCardsButton(sentences)

        val wrappedAnchorElement = document.createElement<HTMLElement>("div").apply {
            style.marginBottom = window.getComputedStyle(anchorElement).marginBottom
                .takeIf { it.isNotBlank() }
                ?: DEFAULT_ANCHOR_MARGIN_BOTTOM
        }
        anchorElement.replaceWith(wrappedAnchorElement)

        wrappedAnchorElement.appendElement("div") {
            className = "add-all-anki-card-container"

            appendChild(anchorElement.removeMargins())
            appendChild(addAllAnkiCardsButton)
        }

        console.debug("Successfully injected 'Add All Sentences' button (${index + 1}/${injectPoints.size})")
    }
}

private fun addAllAnkiCardsButton(sentencesDivs: List<HTMLElement>): HTMLButtonElement =
    document.createElement<HTMLButtonElement>("button").apply {
        className = "add-all-anki-card"
        type = "button"
        textContent = "Adicionar todas"

        // Delegates the card creation to each individual sentence button
        addEventListener("click", {
            console.info("Adding ${sentencesDivs.size} sentences from this page on Anki...")

            sentencesDivs.forEachIndexed { index, sentenceDiv ->
                console.info("Adding Anki card ${index + 1} of ${sentencesDivs.size}")
                handleAddAnkiCardButtonClick(sentenceDiv)
            }
        })
    }

private fun handleAddAnkiCardButtonClick(sentenceDiv: Element) {
    val sentence = extractSentenceFromCard(sentenceDiv)
    scope.launch {
        try {
            requestAnkiAddNewCard(sentence)
        } catch (e: Throwable) {
            console.error("Failed to send ${EventType.ANKI_ADD_CARD} event", e.nonFatalOrThrow())
        }
    }
}

private fun extractSentenceFromCard(sentenceDiv: Element): Sentence {
    val cardDiv = sentenceDiv.querySelector(CARD_DIV_CSS_SELECTOR)
        ?: throw IllegalStateException("Could not find 'card' in sentence div")
    val front = (cardDiv.querySelector(CARD_FRONT_SIDE_CSS_SELECTOR) as? HTMLElement)?.innerHTML?.trimSentence()
        ?: throw IllegalStateException("Could not find sentence of the front of the card")
    val back = (cardDiv.querySelector(CARD_BACK_SIDE_CSS_SELECTOR) as? HTMLElement)?.innerHTML?.trimSentence()
        ?: throw IllegalStateException("Could not find sentence of the back of the card")
    val audioUrl = sentenceDiv.querySelector(SENTENCE_AUDIO_CSS_SELECTOR)?.getAttribute("src")?.trim()
        ?.takeIf { it.isNotBlank() }

    return Sentence(
        front = front,
        back = back,
        audioUrl = audioUrl,
    )
}

private val SURROUNDING_BR_REGEX = """^(?:<br>\s*)*([\s\S]+?)(?:\s*<br>)*$""".toRegex()

private fun String.trimSentence(): String = trim().replace(SURROUNDING_BR_REGEX, "$1")

private const val DEFAULT_ANCHOR_TEXT = "Frases de Exemplo"
private const val DEFAULT_ANCHOR_MARGIN_BOTTOM = "25px"

private const val SENTENCE_DIV_CSS_SELECTOR = "div.sentence"
private const val SENTENCE_BUTTONS_CSS_SELECTOR = "div.port"
private const val SENTENCE_AUDIO_CSS_SELECTOR = "$SENTENCE_BUTTONS_CSS_SELECTOR > audio"

private const val CARD_DIV_CSS_SELECTOR = ".card"
private const val CARD_FRONT_SIDE_CSS_SELECTOR = ".card-front"
private const val CARD_BACK_SIDE_CSS_SELECTOR = ".card-back"

fun Element.isSentenceDiv(): Boolean = tagName.equals("div", ignoreCase = true)
    && classList.contains("sentence")
