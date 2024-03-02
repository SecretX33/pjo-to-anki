@file:Suppress("FunctionName")

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mui.material.Switch
import react.ChildrenBuilder
import react.FC
import react.Props
import react.StateInstance
import react.create
import react.dom.client.createRoot
import react.dom.html.AutoComplete
import react.dom.html.HTMLAttributes
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.span
import react.useState
import web.console.console
import web.html.HTMLElement
import web.html.InputType
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

const val ANKI_CONNECT_ADDON_LINK = "https://ankiweb.net/shared/info/2055492159"
const val SAVE_CONFIG_DEFAULT_TEXT = "Salvar configurações"

fun main() {
    val container = web.dom.document.getElementById("root") ?: throw IllegalStateException("Couldn't find root div!")
    createRoot(container).render(Options.create())
}

@Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
private val Options = FC {
    val config = useSuspendState { globalConfig() }

    if (config.value == null) {
        p { +"Carregando..." }
        return@FC
    }
    config as StateInstance<ExtensionConfig>

    val handleSaveConfig = suspend {
        try {
            val trimmedConfig = config.value.trimmed().also { config.value = it }
            setGlobalConfig(trimmedConfig)
            console.info("Saved config:", trimmedConfig.toNormalJsObject())
            notifySuccess("As configurações foram salvas com sucesso.", title = "Configurações salvas", useEvent = false)
        } catch (e: Throwable) {
            console.error("Could not save config", config.value.toNormalJsObject(), e.nonFatalOrThrow())
            throw e
        }
    }

    val props: ConfigProps.() -> Unit = {
        this.config = config
        this.saveConfig = handleSaveConfig
    }

    form {
        div {
            classNameString = "form__div"
            AnkiOptions(props)
            NotificationOptions(props)
            SaveButtons(props)
        }
    }
}

private val AnkiOptions = FC<ConfigProps> { props ->
    var config by props.config
    var ankiConnect by derivedState({ config.ankiConnect }, { config = config.copy(ankiConnect = it) })
    var deckConfig by derivedState({ ankiConnect.deckConfig }, { ankiConnect = ankiConnect.copy(deckConfig = it) })

    section {
        classNameString = "form_category__section"
        h1 { +"Conexão com o Anki" }

        p {
            classNameString = "form_category_desc__p"
            +"Ajuste a integração com o Anki, desde a URL até informações do seu deck."
        }

        section {
            classNameString = "form_category_items__section"
            section {
                h3 { +"Gerais" }

                div {
                    classNameString = "form_inner_category__div"
                    formEntry {
                        label {
                            +"URL do Anki Connect ("
                            a {
                                href = ANKI_CONNECT_ADDON_LINK
                                +"link"
                            }
                            +")"
                        }
                    }
                    formEntry {
                        textInput(
                            id = "ankiUrl",
                            getValue = { ankiConnect.url },
                            setValue = { ankiConnect = ankiConnect.copy(url = it) },
                            autoComplete = AutoComplete.url,
                        )
                    }
                }
            }

            section {
                h3 { +"Deck" }

                div {
                    classNameString = "form_inner_category__div"

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

                    formEntry({ textLabel("Nome do campo de áudio", forId = "audioFieldName") }, {
                        textInput(
                            id = "audioFieldName",
                            getValue = { deckConfig.audioFieldName.orEmpty() },
                            setValue = { deckConfig = deckConfig.copy(audioFieldName = it) },
                        )
                    })

                    formEntry({
                        textLabel("Tags das cartas", forId = "cardTags")
                        p {
                            classNameString = "field_description"
                            +"Separe as tags por vírgula. Exemplo: tag1, tag2, tag3."
                        }
                    }, {
                        textInput(
                            id = "cardTags",
                            getValue = { deckConfig.tags.joinToString(", ") { it.trimStart() } },
                            setValue = { deckConfig = deckConfig.copy(tags = it.split(",").toSet()) },
                        )
                    })

                    formEntry("flex-row") {
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

private val NotificationOptions = FC<ConfigProps> { props ->
    var config by props.config
    var notificationConfig by derivedState({ config.notificationConfig }, { config = config.copy(notificationConfig = it) })

    section {
        classNameString = "form_category__section"

        h1 { +"Notificações" }

        p {
            classNameString = "form_category_desc__p"
            +"Ative ou desative notificações, ajuste o tempo que elas ficam visíveis, etc."
        }

        section {
            classNameString = "form_category_items__section"

            section {
                h3 { +"Sucesso" }

                div {
                    classNameString = "form_inner_category__div"

                    formEntry("flex-row") {
                        textLabel("Mostrar notificações de sucesso: ", forId = "showOnSuccess")
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

            section {
                h3 { +"Aviso" }

                div {
                    classNameString = "form_inner_category__div"

                    formEntry("flex-row") {
                        textLabel("Mostrar notificações de aviso: ", forId = "showOnWarning")
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

            section {
                h3 { +"Erro" }

                div {
                    classNameString = "form_inner_category__div"

                    formEntry("flex-row") {
                        textLabel("Mostrar notificações de erro: ", forId = "showOnError")
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

private val SaveButtons = FC<ConfigProps> { props ->
    var config by props.config
    var saveConfigText by useState(SAVE_CONFIG_DEFAULT_TEXT)
    var saveButtonEnabled by useState(true)

    div {
        classNameString = "action_buttons__div"

        formEntry {
            button {
                id = "save_config__button"
                classNameString = "action_button_style"
                onClick = eventHandler@ {
                    it.preventDefault()
                    if (!saveButtonEnabled) return@eventHandler

                    mainScope.launch {
                        runCatching { props.saveConfig() }
                            .onSuccess {
                                saveButtonEnabled = false
                                saveConfigText = "Sucesso ✔"
                                delay(config.notificationConfig.successTimeout)
                                saveButtonEnabled = true
                                saveConfigText = SAVE_CONFIG_DEFAULT_TEXT
                            }.onFailure {
                                it.nonFatalOrThrow()
                                saveButtonEnabled = false
                                saveConfigText = "Erro ao salvar configurações ✖"
                                delay(2.seconds)
                                saveButtonEnabled = true
                                saveConfigText = SAVE_CONFIG_DEFAULT_TEXT
                            }.getOrThrow()
                    }
                }
                +saveConfigText
            }
        }

        formEntry {
            button {
                id = "reset_config__button"
                classNameString = "action_button_style"
                onClick = {
                    it.preventDefault()
                    config = createDefaultConfig()
                    notifySuccess("As configurações foram redefinidas com sucesso. Não se esqueça de salvar.", title = "Configurações redefinidas", useEvent = false, timeout = config.notificationConfig.successTimeout + 700.milliseconds)
                }
                +"Redefinir configurações"
            }
        }
    }
}

private fun ChildrenBuilder.formEntry(
    vararg classes: String,
    block: HTMLAttributes<out HTMLElement>.() -> Unit,
) {
    span {
        classNameString = (arrayOf("form_entry__span") + classes).joinToString(" ")
        block()
    }
}

private fun ChildrenBuilder.formEntry(
    buildLabel: HTMLAttributes<out HTMLElement>.() -> Unit,
    buildInput: HTMLAttributes<out HTMLElement>.() -> Unit,
) {
    formEntry {
        buildLabel()
        br
        buildInput()
    }
}

private fun ChildrenBuilder.textLabel(text: String, forId: String? = null) {
    label {
        htmlFor = forId
        +text
    }
}

private fun ChildrenBuilder.textInput(
    id: String,
    getValue: () -> String,
    setValue: (String) -> Unit,
    autoComplete: AutoComplete = AutoComplete.on,
    editable: Boolean = true,
    spellCheck: Boolean = false,
) {
    input {
        this.id = id
        this.name = id
        this.spellCheck = spellCheck
        this.autoComplete = autoComplete
        contentEditable = editable
        type = InputType.text
        value = getValue()
        onInput = { setValue(it.currentTarget.value) }
    }
}

private fun ChildrenBuilder.numberInput(
    id: String,
    getValue: () -> Int,
    setValue: (Int) -> Unit,
    editable: Boolean = true,
) {
    input {
        this.id = id
        name = id
        spellCheck = spellCheck
        autoComplete = AutoComplete.off
        contentEditable = editable
        type = InputType.number
        value = getValue()
        onInput = { setValue(it.currentTarget.value.toIntOrNull() ?: 0) }
    }
}

private fun ChildrenBuilder.checkboxInput(
    id: String,
    getValue: () -> Boolean,
    setValue: (Boolean) -> Unit,
) {
    Switch {
        this.id = id
        name = id
        checked = getValue()
        onClick = { setValue(!(checked ?: false)) }
    }
}

external interface ConfigProps : Props {
    var config: StateInstance<ExtensionConfig>
    var saveConfig: suspend () -> Unit
}
