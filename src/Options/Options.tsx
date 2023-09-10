import "./styles.scss";
import React, {useEffect, useState} from "react";
import {globalConfig, setGlobalConfig} from "../common/storage";
import {EMPTY_GLOBAL_CONFIG} from "../common/config";
import {notify} from "../common/notification.ts";

function Options(): React.JSX.Element {
    const [config, setConfig] = useState(EMPTY_GLOBAL_CONFIG);

    useEffect(() => {
        globalConfig().then(config => {
            setConfig(config);
            console.debug("Loaded config is", config);
        });
    }, []);

    if (config === EMPTY_GLOBAL_CONFIG) {
        return (
            <p>Carregando...</p>
        );
    }

    const handleSaveConfig = () => {
        console.info("Saving config", config);
        setGlobalConfig(config).then(_ => notify("Configurações salvas", "As configurações foram salvas com sucesso."));
    }

    return (
        <div>
            <b><h1>Conexão com o Anki</h1></b>
            <form>
                <p className="form_entry__p">
                    <label htmlFor="ankiUrl">URL do Anki Connect (<a href="https://ankiweb.net/shared/info/2055492159">link</a>)</label>
                    <br/>
                    <input
                        type="text"
                        id="ankiUrl"
                        name="ankiUrl"
                        spellCheck="false"
                        autoComplete="off"
                        value={config.ankiConnect.url}
                        onChange={(e) => {
                            const newUrl = e.target.value;
                            setConfig({
                                ...config,
                                ankiConnect: {
                                    ...config.ankiConnect,
                                    url: newUrl,
                                }
                            })
                        }}
                    />
                </p>

                <p className="form_entry__p">
                    <label htmlFor="deckName">Nome do deck</label>
                    <br/>
                    <input
                        type="text"
                        id="deckName"
                        name="deckName"
                        spellCheck="false"
                        autoComplete="off"
                        value={config.ankiConnect.deckConfig.name}
                        onChange={(e) => {
                            const newName = e.target.value;
                            setConfig({
                                ...config,
                                ankiConnect: {
                                    ...config.ankiConnect,
                                    deckConfig: {
                                        ...config.ankiConnect.deckConfig,
                                        name: newName,
                                    }
                                }
                            })
                        }}
                    />
                </p>

                <p className="form_entry__p">
                    <label htmlFor="modelName">Tipo de nota</label>
                    <br/>
                    <input
                        type="text"
                        id="modelName"
                        name="modelName"
                        spellCheck="false"
                        autoComplete="off"
                        value={config.ankiConnect.deckConfig.modelName}
                        onChange={(e) => {
                            const modelName = e.target.value;
                            setConfig({
                                ...config,
                                ankiConnect: {
                                    ...config.ankiConnect,
                                    deckConfig: {
                                        ...config.ankiConnect.deckConfig,
                                        modelName
                                    }
                                }
                            })
                        }}
                    />
                </p>

                <p className="form_entry__p">
                    <label htmlFor="frontFieldName">Nome do campo da frente (do tipo de nota)</label>
                    <br/>
                    <input
                        type="text"
                        id="frontFieldName"
                        name="frontFieldName"
                        spellCheck="false"
                        autoComplete="off"
                        value={config.ankiConnect.deckConfig.frontFieldName}
                        onChange={(e) => {
                            const frontFieldName = e.target.value;
                            setConfig({
                                ...config,
                                ankiConnect: {
                                    ...config.ankiConnect,
                                    deckConfig: {
                                        ...config.ankiConnect.deckConfig,
                                        frontFieldName: frontFieldName
                                    }
                                }
                            })
                        }}
                    />
                </p>

                <p className="form_entry__p">
                    <label htmlFor="backFieldName">Nome do campo do verso (do tipo de nota)</label>
                    <br/>
                    <input
                        type="text"
                        id="backFieldName"
                        name="backFieldName"
                        spellCheck="false"
                        autoComplete="off"
                        value={config.ankiConnect.deckConfig.backFieldName}
                        onChange={(e) => {
                            const backFieldName = e.target.value;
                            setConfig({
                                ...config,
                                ankiConnect: {
                                    ...config.ankiConnect,
                                    deckConfig: {
                                        ...config.ankiConnect.deckConfig,
                                        backFieldName: backFieldName,
                                    }
                                }
                            })
                        }}
                    />
                </p>

                <p className="form_entry__p">
                    <label htmlFor="checkDuplicatedsInSubdecks">Checar por cartas duplicadas em subdecks: </label>
                    <input
                        type="checkbox"
                        id="checkDuplicatedsInSubdecks"
                        name="checkDuplicatedsInSubdecks"
                        defaultChecked={config.ankiConnect.deckConfig.checkDuplicatedsInSubdecks}
                        onChange={(e) => {
                            const checkDuplicatedsInSubdecks = !e.target.value;
                            setConfig({
                                ...config,
                                ankiConnect: {
                                    ...config.ankiConnect,
                                    deckConfig: {
                                        ...config.ankiConnect.deckConfig,
                                        checkDuplicatedsInSubdecks
                                    }
                                }
                            })
                        }}
                    />
                </p>

                <p className="form_entry__p">
                    <button id="save_config__button" type="button" onClick={handleSaveConfig}>Salvar configurações</button>
                </p>
            </form>
        </div>
    );
}

export default Options;
