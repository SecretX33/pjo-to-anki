import androidx.compose.runtime.NoLiveLiterals
import chrome.tabs.CreateProperties
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable("root") {
        Div({ id("popup") }) {
            H2 { Text("PJO to Anki") }
            Button({
                id("open_config__button")
                onClick {
                    openWebPage("options.html")
                }
            }) {
                P { Text("Configurações") }
            }
        }
    }
}

@NoLiveLiterals
private fun openWebPage(url: String) {
    val properties = jsObject<CreateProperties>().also { it.url = url }
    chrome.tabs.create(properties)
}
