import {Notifications} from "webextension-polyfill-ts";
import {NOTIFICATION_EVENT_TYPE, sendEvent} from "./event.ts";

export type Notification = Notifications.CreateNotificationOptions;
export type TemplateType = Notifications.TemplateType;

export function notify(
    title: string,
    message: string,
    iconUrl = "/assets/icons/anki_icon.svg",
    type: TemplateType = "basic",
): void {
    const notification: Notification = {
        type,
        title,
        message,
        iconUrl
    }
    sendEvent({
        type: NOTIFICATION_EVENT_TYPE,
        content: notification,
    });
}
