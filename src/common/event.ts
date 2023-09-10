import {browser} from "webextension-polyfill-ts";
import {Notification} from "./notification.ts";
import {AnkiAddCard} from "./anki_connect.ts";

export const NOTIFICATION_EVENT_TYPE = "notification";
export const ANKI_ADD_CARD_EVENT_TYPE = "anki_add_card";

export type EventType = typeof NOTIFICATION_EVENT_TYPE | typeof ANKI_ADD_CARD_EVENT_TYPE;
export type EventContent = Notification | AnkiAddCard;

export interface Event<T extends EventContent> {
    type: EventType,
    content: T,
}

export type EventHandler = (event: Event<EventContent>) => void;

export function sendEvent(event: Event<EventContent>) {
    console.debug("Sending event", event);
    browser.runtime.sendMessage("", event);
}