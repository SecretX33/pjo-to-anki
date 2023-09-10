import {DeckConfig} from "./config.ts";
import {ANKI_ADD_CARD_EVENT_TYPE} from "./event.ts";
import {globalConfig} from "./storage.ts";
import {AxiosRequestConfig} from "axios";
import {axiosInstance} from "./network.ts";
import {notify} from "./notification.ts";

const ANKI_CONNECT_ACTION_NAME = {
    [ANKI_ADD_CARD_EVENT_TYPE]: "addNote",
} satisfies Record<AnkiConnectAction, string>

const ANKI_API_VERSION: number = 6;

export type AnkiConnectAction = "anki_add_card";

export interface AnkiAddCard {
    sentence: Sentence,
    deck: DeckConfig,
}

export interface Sentence {
    front: string,
    back: string,
}

export async function requestAnkiAddNewCard(sentence: Sentence) {
    console.debug(`Request to add new card to Anki will be send`, sentence);

    const deck = (await globalConfig()).ankiConnect.deckConfig;
    const actionName = ANKI_CONNECT_ACTION_NAME[ANKI_ADD_CARD_EVENT_TYPE];
    const params = {
        "note": {
            "deckName": deck.name,
            "modelName": deck.modelName,
            "fields": {
                [deck.frontFieldName]: sentence.front,
                [deck.backFieldName]: sentence.back,
            },
            "options": {
                "allowDuplicate": false,
                "duplicateScope": "deck",
                "duplicateScopeOptions": {
                    "deckName": deck.name,
                    "checkChildren": deck.checkDuplicatedsInSubdecks,
                    "checkAllModels": false
                }
            },
            "tags": []
        }
    };

    ankiRequest(actionName, params)
        .then(() => {
            console.info("Card created on Anki via AnkiConnect");
            notify("Carta adicionada", "A carta com a frase selecionada foi criada com sucesso.");
        })
        .catch(e => {
            const isDuplicateCardError = String(e).toLowerCase().includes("cannot create note because it is a duplicate");
            if (isDuplicateCardError) {
                notify("Carta duplicada", "Essa carta já faz parte do seu baralho.");
            } else {
                // General error
                console.error(`Request to Anki failed!`, e);
                notify("Erro", `Não foi possível criar a carta no Anki.\nErro: ${e?.message ?? e}`);
            }
        });
}

interface AnkiResponse<T> {
    result: T,
    error: string | null,
}

async function ankiRequest<T>(action: string, params = {}, version: number = ANKI_API_VERSION): Promise<T> {
    const body = JSON.stringify({ action, version, params });
    const extensionConfig = await globalConfig();

    const requestConfig: AxiosRequestConfig = {
        url: extensionConfig.ankiConnect.url,
        method: "POST",
        data: body,
    };
    const response = await axiosInstance.request<AnkiResponse<never>>(requestConfig)
        .catch(e => {
            console.error(e);
            throw e;
        });

    const data = response.data;

    if (Object.getOwnPropertyNames(data).length !== 2) {
        throw new Error("Response has an unexpected number of fields");
    }

    if (!Object.hasOwn(data, "result")) {
        throw new Error("Response is missing required result field");
    }

    if (!Object.hasOwn(data, "error")) {
        throw new Error("Response is missing required error field");
    }

    if (data.error) {
        throw new Error(data.error);
    }

    return data.result;
}