import {browser} from "webextension-polyfill-ts";

export function cssCopyCat(elem1: HTMLElement, elem2: HTMLElement): void {
    // get computed styles of original element
    const styles = window.getComputedStyle(elem1);

    let {cssText} = styles;
    if (!cssText) {
        cssText = Array.from(styles).reduce((str, property) => {
            return `${str}${property}:${styles.getPropertyValue(property)};`;
        }, "");
    }

    // assign css styles to element
    elem2.style.cssText = cssText;
}

export function findResourceUrl(relativePath: string): string {
    return browser.runtime.getURL(relativePath);
}