export interface DeckConfig {
    name: string,
    modelName: string,
    frontFieldName: string,
    backFieldName: string,
    checkDuplicatedsInSubdecks: boolean,
}

export interface AnkiConnectConfig {
    url: string,
    deckConfig: DeckConfig,
}

export interface ExtensionConfig {
    version: number,
    ankiConnect: AnkiConnectConfig,
}

export const EMPTY_GLOBAL_CONFIG: ExtensionConfig = {
    version: 1,
    ankiConnect: {
        url: "",
        deckConfig: {
            name: "",
            modelName: "",
            frontFieldName: "",
            backFieldName: "",
            checkDuplicatedsInSubdecks: true,
        }
    }
}