import {browser} from "webextension-polyfill-ts";
import {EMPTY_GLOBAL_CONFIG, ExtensionConfig} from "./config";

const GLOBAL_CONFIG_KEY = "globalConfig";

function createConfig(): ExtensionConfig {
    return {
        version: EMPTY_GLOBAL_CONFIG.version,
        ankiConnect: {
            url: "http://localhost:8765",
            deckConfig: {
                name: "Frases em Japonês",
                modelName: "Básico",
                frontFieldName: "Frente",
                backFieldName: "Verso",
                checkDuplicatedsInSubdecks: true,
            }
        },
    };
}

export async function globalConfig(): Promise<ExtensionConfig> {
    const localStorage = browser.storage.local;
    const existingConfig = (await localStorage.get(GLOBAL_CONFIG_KEY))?.globalConfig as ExtensionConfig | null;
    if (existingConfig != null && Object.keys(existingConfig).length > 0) {
        console.debug("Found existing config:", existingConfig)
        return existingConfig;
    }

    const config = createConfig();
    await localStorage.set({ [GLOBAL_CONFIG_KEY]: config });
    return config;
}

export function setGlobalConfig(config: ExtensionConfig): Promise<void> {
    const localStorage = browser.storage.local;
    return localStorage.set({ [GLOBAL_CONFIG_KEY]: config });
}