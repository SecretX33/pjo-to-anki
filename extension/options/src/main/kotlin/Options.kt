import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.CheckboxInput
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.NumberInput
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput
import org.jetbrains.compose.web.renderComposable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

const val ANKI_CONNECT_ADDON_LINK = "https://ankiweb.net/shared/info/2055492159"
const val SAVE_CONFIG_DEFAULT_TEXT = "Salvar configurações"

fun main() {
    renderComposable("root") {
        Options()
    }
}

@Composable
private fun Options() {
    val _config by produceState(null as ExtensionConfig?) { value = globalConfig() }

    if (_config == null) {
        P { Text("Carregando...") }
        return
    }

    val config = remember { mutableStateOf(_config!!) }

    val handleSaveConfig = suspend {
        try {
            config.value = config.value.trimmed()
            setGlobalConfig(config.value)
            console.info("Saved config:", config.value.toNormalJsObject())
        } catch (e: Throwable) {
            console.error("Could not save config", config.value.toNormalJsObject(), e.nonFatalOrThrow())
            throw e
        }
    }

    Form {
        Div({ classes("form__div") }) {
            AnkiOptions(config)

            NotificationOptions(config)

            Div({ classes("action_buttons__div") }) {
                var saveConfigText by remember { mutableStateOf(SAVE_CONFIG_DEFAULT_TEXT) }
                formEntry {
                    Button({
                        id("save_config__button")
                        classes("action_button_style")
                        onClick {
                            it.preventDefault()
                            scope.launch {
                                runCatching { handleSaveConfig() }
                                    .onSuccess {
                                        saveConfigText = "Sucesso ✔"
                                        delay(1250.milliseconds)
                                        saveConfigText = SAVE_CONFIG_DEFAULT_TEXT
                                    }.onFailure {
                                        saveConfigText = "Erro ao salvar configurações ✖"
                                        delay(2.seconds)
                                        saveConfigText = SAVE_CONFIG_DEFAULT_TEXT
                                    }.getOrThrow()
                            }
                        }
                    }) {
                        Text(saveConfigText)
                    }
                }

                formEntry {
                    Button({
                        id("reset_config__button")
                        classes("action_button_style")
                        onClick {
                            it.preventDefault()
                            config.value = createDefaultConfig()
                            notifySuccess("As configurações foram redefinidas com sucesso. Não se esqueça de salvar.", title = "Configurações redefinidas", useEvent = false, timeout = config.value.notificationConfig.successTimeout + 700.milliseconds)
                        }
                    }) {
                        Text("Redefinir configurações")
                    }
                }
            }
        }
    }
}

@Composable
private fun AnkiOptions(derivedConfig: MutableState<ExtensionConfig>) {
    var config by derivedConfig
    var ankiConnect by DerivedState({ config.ankiConnect }, { config = config.copy(ankiConnect = it) })
    var deckConfig by DerivedState({ ankiConnect.deckConfig }, { ankiConnect = ankiConnect.copy(deckConfig = it) })

    Section({ classes("form_category__section") }) {
        H1 { Text("Conexão com o Anki") }

        P({ classes("form_category_desc__p") }) {
            Text("Ajuste a integração com o Anki, desde a URL até informações do seu deck.")
        }

        Section({ classes("form_category_items__section") }) {
            Section {
                H3 { Text("Gerais") }

                Div({ classes("form_inner_category__div") }) {
                    formEntry({
                        Label {
                            Text("URL do Anki Connect (")
                            A(ANKI_CONNECT_ADDON_LINK) { Text("link") }
                            Text(")")
                        }
                    }, {
                        textInput(
                            id = "ankiUrl",
                            getValue = { ankiConnect.url },
                            setValue = { ankiConnect = ankiConnect.copy(url = it) },
                            autoComplete = AutoComplete.url,
                        )
                    })
                }
            }

            Section {
                H3 { Text("Deck") }

                Div({ classes("form_inner_category__div") }) {

                    formEntry({ textLabel("Nome do deck", forId = "deckName") }, {
                        textInput(
                            id = "deckName",
                            getValue = { deckConfig.name },
                            setValue = { deckConfig = deckConfig.copy(name = it) },
                        )
                    })

                    formEntry({ textLabel("Tipo de nota", forId = "modelName") }, {
                        textInput(
                            id = "modelName",
                            getValue = { deckConfig.modelName },
                            setValue = { deckConfig = deckConfig.copy(modelName = it) },
                        )
                    })

                    formEntry({ textLabel("Nome do campo da frente (do tipo de nota)", forId = "frontFieldName") }, {
                        textInput(
                            id = "frontFieldName",
                            getValue = { deckConfig.frontFieldName },
                            setValue = { deckConfig = deckConfig.copy(frontFieldName = it) },
                        )
                    })

                    formEntry({ textLabel("Nome do campo da verso (do tipo de nota)", forId = "backFieldName") }, {
                        textInput(
                            id = "backFieldName",
                            getValue = { deckConfig.backFieldName },
                            setValue = { deckConfig = deckConfig.copy(backFieldName = it) },
                        )
                    })

                    formEntry({ textLabel("Nome do campo de audio", forId = "audioFieldName") }, {
                        textInput(
                            id = "audioFieldName",
                            getValue = { deckConfig.audioFieldName.orEmpty() },
                            setValue = { deckConfig = deckConfig.copy(audioFieldName = it) },
                        )
                    })

                    formEntry {
                        textLabel("Checar por cartas duplicadas em subdecks: ", forId = "checkDuplicatedsInSubdecks")
                        checkboxInput(
                            id = "checkDuplicatedsInSubdecks",
                            getValue = { deckConfig.checkDuplicatedsInSubdecks },
                            setValue = { deckConfig = deckConfig.copy(checkDuplicatedsInSubdecks = it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationOptions(derivedConfig: MutableState<ExtensionConfig>) {
    var config by derivedConfig
    var notificationConfig by DerivedState({ config.notificationConfig }, { config = config.copy(notificationConfig = it) })

    Section({ classes("form_category__section") }) {
        H1 { Text("Notificações") }

        P({ classes("form_category_desc__p") }) {
            Text("Ative ou desative notificações, ajuste o tempo que elas ficam visíveis, etc.")
        }

        Section({ classes("form_category_items__section") }) {

            Section {
                H3 { Text("Sucesso") }

                Div({ classes("form_inner_category__div") }) {
                    formEntry {
                        textLabel("Mostrar notifições de sucesso: ", forId = "showOnSuccess")
                        checkboxInput(
                            id = "showOnSuccess",
                            getValue = { notificationConfig.showOnSuccess },
                            setValue = { notificationConfig = notificationConfig.copy(showOnSuccess = it) },
                        )
                    }

                    formEntry({ textLabel("Timeout das notificações de sucesso (em ms)", forId = "successTimeout") }, {
                        numberInput(
                            id = "successTimeout",
                            getValue = { notificationConfig.successTimeout.inWholeMilliseconds.toInt() },
                            setValue = { notificationConfig = notificationConfig.copy(successTimeout = it.milliseconds) },
                        )
                    })
                }
            }

            Section {
                H3 { Text("Aviso") }

                Div({ classes("form_inner_category__div") }) {
                    formEntry {
                        textLabel("Mostrar notifições de aviso: ", forId = "showOnWarning")
                        checkboxInput(
                            id = "showOnWarning",
                            getValue = { notificationConfig.showOnWarning },
                            setValue = { notificationConfig = notificationConfig.copy(showOnWarning = it) },
                        )
                    }

                    formEntry({ textLabel("Timeout das notificações de aviso (em ms)", forId = "warningTimeout") }, {
                        numberInput(
                            id = "warningTimeout",
                            getValue = { notificationConfig.warningTimeout.inWholeMilliseconds.toInt() },
                            setValue = { notificationConfig = notificationConfig.copy(warningTimeout = it.milliseconds) },
                        )
                    })
                }
            }

            Section {
                H3 { Text("Erro") }

                Div({ classes("form_inner_category__div") }) {
                    formEntry {
                        textLabel("Mostrar notifições de erro: ", forId = "showOnError")
                        checkboxInput(
                            id = "showOnError",
                            getValue = { notificationConfig.showOnError },
                            setValue = { notificationConfig = notificationConfig.copy(showOnError = it) },
                        )
                    }

                    formEntry({ textLabel("Timeout das notificações de erro (em ms)", forId = "errorTimeout") }, {
                        numberInput(
                            id = "errorTimeout",
                            getValue = { notificationConfig.errorTimeout.inWholeMilliseconds.toInt() },
                            setValue = { notificationConfig = notificationConfig.copy(errorTimeout = it.milliseconds) },
                        )
                    })
                }
            }
        }
    }
}

@Composable
private fun formEntry(block: @Composable () -> Unit) {
    P({ classes("form_entry__p") }) {
        block()
    }
}

@Composable
private fun formEntry(
    buildLabel: @Composable () -> Unit,
    buildInput: @Composable () -> Unit,
) {
    formEntry {
        buildLabel()
        Br()
        buildInput()
    }
}

@Composable
private fun textLabel(text: String, forId: String? = null) {
    Label(forId = forId) { Text(text) }
}

@Composable
private fun textInput(
    id: String,
    getValue: () -> String,
    setValue: (String) -> Unit,
    autoComplete: AutoComplete = AutoComplete.on,
    editable: Boolean = true,
    spellCheck: Boolean = false,
) {
    TextInput(getValue()) {
        id(id)
        name(id)
        spellCheck(spellCheck)
        autoComplete(autoComplete)
        contentEditable(editable)
        onInput { setValue(it.value) }
    }
}

@Composable
private fun numberInput(
    id: String,
    getValue: () -> Int,
    setValue: (Int) -> Unit,
    editable: Boolean = true,
) {
    NumberInput(getValue()) {
        id(id)
        name(id)
        spellCheck(false)
        autoComplete(AutoComplete.off)
        contentEditable(editable)
        onInput {
            setValue(it.value?.toInt() ?: 0)
        }
    }
}

@Composable
private fun checkboxInput(
    id: String,
    getValue: () -> Boolean,
    setValue: (Boolean) -> Unit,
) {
    CheckboxInput(getValue()) {
        id(id)
        name(id)
        onInput {
            setValue(it.value)
        }
    }
}

private class DerivedState<T>(
    private val getValue: () -> T,
    private val updateValue: (T) -> Unit,
) : MutableState<T>, ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = updateValue(value)
    override var value: T
        get() = getValue()
        set(value) { updateValue(value) }
    override fun component1(): T = getValue()
    override fun component2(): (T) -> Unit = { updateValue(it) }
}
