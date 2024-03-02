import chrome.tabs.CreateProperties
import react.FC
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.p

fun main() {
    val container = web.dom.document.getElementById("root") ?: throw IllegalStateException("Couldn't find root div!")
    createRoot(container).render(Popup.create())
}

private val Popup = FC {
    div {
        id = "popup"
        h2 {
            +"PJO to Anki"
        }
        button {
            id = "open_config__button"
            onClick = {
                openWebPage("options.html")
            }
            p {
                +"Configurações"
            }
        }
    }
}

private fun openWebPage(url: String) {
    val properties = jsObject<CreateProperties>().also { it.url = url }
    chrome.tabs.create(properties)
}
