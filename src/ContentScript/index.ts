import {requestAnkiAddNewCard, Sentence} from "../common/anki_connect.ts";
import {findResourceUrl} from "../common/util.ts";

((): void => {
    const sentencesDivs = Array.from(document.querySelectorAll("div.sentence"));
    console.debug("Sentences found on page:", sentencesDivs);

    for (const sentenceDiv of sentencesDivs) {
        const buttonsDiv = sentenceDiv.querySelector("div.port");
        if (buttonsDiv == null) {
            console.error("Didn't find buttons div!");
            break;
        }

        const addAnkiCardSpan = document.createElement("span");
        addAnkiCardSpan.style.setProperty("background-image", `url('${findResourceUrl("/assets/icons/plus_icon.png")}')`);
        addAnkiCardSpan.style.setProperty("background-size", "100% 100%");
        addAnkiCardSpan.style.setProperty("margin-left", "0.8rem");
        addAnkiCardSpan.style.setProperty("display", "inline-table");
        addAnkiCardSpan.style.setProperty("height", "0.8rem");
        addAnkiCardSpan.style.setProperty("width", "0.8rem");
        addAnkiCardSpan.style.setProperty("cursor", "pointer");
        buttonsDiv.insertBefore(addAnkiCardSpan, buttonsDiv.querySelector(".expand-card"));
        addAnkiCardSpan.addEventListener("click", () => handleAddAnkiCardButtonClick(sentenceDiv));
    }
})();

async function handleAddAnkiCardButtonClick(sentenceDiv: Element): Promise<void> {
    const sentence = extractSentenceFromCard(sentenceDiv.querySelector(".card") as Element);
    await requestAnkiAddNewCard(sentence);
}

function extractSentenceFromCard(cardDiv: Element): Sentence {
    const front = (cardDiv.querySelector(".card-front") as HTMLElement).innerText;
    const back = (cardDiv.querySelector(".card-back") as HTMLElement).innerText;
    return { front, back };

}

export {};