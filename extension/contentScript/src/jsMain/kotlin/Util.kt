import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

@Suppress("UNCHECKED_CAST")
fun <T : Element> Document.createElement(tagName: String): T = createElement(tagName) as T

fun <T : Element> T.removeMargins(): T = apply {
    (this as? HTMLElement)?.style?.setProperty("margin", "0")
}
