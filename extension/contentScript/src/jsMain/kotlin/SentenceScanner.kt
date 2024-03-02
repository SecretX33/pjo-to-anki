import org.w3c.dom.HTMLElement

private val SCAN_TITLE_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6")
private val SCAN_IGNORED_TAGS = setOf("p")

sealed class AnchorPoint {
    data object Missing : AnchorPoint()
    data class Title(val titleElement: HTMLElement) : AnchorPoint()
}

/**
 * Represents a group of sentences that should have an "add all" button above [anchor], and when clicked,
 * all [sentences] must be added to Anki as cards.
 *
 * If [anchor] is [AnchorPoint.Missing], then a default anchor tag must be added first, then the "add all" button
 * must be injected as if the anchor was part of the original page.
 */
data class AddAllSentencesInjectPoints(
    val anchor: AnchorPoint,
    val sentences: Collection<HTMLElement>,
) {
    init {
        require(sentences.isNotEmpty()) { "Sentences must not be empty" }
    }
}

/**
 * Finds all the sentences in the given [sentences] collection, and returns a list of [AddAllSentencesInjectPoints] that
 * represents the groups of sentences that should have an "add all" button above them.
 */
fun findAddAllSentencesInjectPoints(sentences: Collection<HTMLElement>): Collection<AddAllSentencesInjectPoints> {
    if (sentences.isEmpty()) return emptyList()

    val addAllSentencesInjectPoints = mutableSetOf<AddAllSentencesInjectPoints>()
    val deque = ArrayDeque<HTMLElement>().apply { addAll(sentences) }

    while (deque.isNotEmpty()) {
        val sentence = deque.removeFirst()
        val sentencesInSection = sentence.getSectionSentences()
        console.debug("Sentences in section:", sentencesInSection.toTypedArray())
        deque.removeAll(sentencesInSection)

        addAllSentencesInjectPoints += AddAllSentencesInjectPoints(
            anchor = findAnchorPoint(sentence),
            sentences = sentencesInSection,
        )
    }
    return addAllSentencesInjectPoints
}

private fun HTMLElement.getSectionSentences(): Collection<HTMLElement> =
    generateSequence(this) { it.nextElementSibling as? HTMLElement }
        .filter { it.tagNameLowercase !in SCAN_IGNORED_TAGS }
        .takeWhile { it.isSentenceDiv() }
        .toSet()

private fun findAnchorPoint(sentence: HTMLElement): AnchorPoint {
    val anchorElement = (sentence.previousElementSibling as? HTMLElement)
        ?.takeIf { it.tagNameLowercase in SCAN_TITLE_TAGS }
    return when (anchorElement) {
        null -> AnchorPoint.Missing
        else -> AnchorPoint.Title(anchorElement)
    }
}
