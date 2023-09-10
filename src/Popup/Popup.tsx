import * as React from "react";
import {browser, Tabs} from "webextension-polyfill-ts";

import "./styles.scss";

function openWebPage(url: string): Promise<Tabs.Tab> {
    return browser.tabs.create({url});
}

export function Popup() {
    return (
        <section id="popup">
            <h2>PJO to Anki</h2>
            <button
                id="open_config__button"
                type="button"
                onClick={(): Promise<Tabs.Tab> => openWebPage("options.html")}
            >Configurações
            </button>
        </section>
    );
}