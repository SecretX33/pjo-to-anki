import "emoji-log";
import {browser, Runtime} from "webextension-polyfill-ts";
import {Event, EventContent, EventHandler, NOTIFICATION_EVENT_TYPE} from "../common/event.ts";
import {Notification} from "../common/notification.ts";
import {findResourceUrl} from "../common/util.ts";

const eventHandlers: { [a: string]: EventHandler } = {
    [NOTIFICATION_EVENT_TYPE]: (event: Event<EventContent>) => sendNotification(event.content as Notification),
}

browser.runtime.onInstalled.addListener((): void => {
    console.emoji("🦄", "extension installed");
});

browser.runtime.onMessage.addListener((event, sender) => {
    onEventReceived(event, sender);
});

function sendNotification(notification: Notification) {
    const notificationWithFullUrl: Notification = {
        ...notification,
        iconUrl: findResourceUrl(notification.iconUrl!),
    };
    browser.notifications.create(undefined, notificationWithFullUrl)
        .catch(e => console.error("Create notification failed!", e));
}

function onEventReceived(event: Event<EventContent>, sender: Runtime.MessageSender) {
    const handler = eventHandlers[event.type];
    console.debug("Event has been received:", event, "\nAnd sender is:", sender, "\nAnd handler is:", handler);
    if (handler != null) {
        handler(event);
    }
}
