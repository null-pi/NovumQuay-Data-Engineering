package com.utils.novumquay.scrapper;

import static com.codeborne.selenide.Selenide.*;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.*;

public class Scrapper {
    private String url;

    public Scrapper(String url) {
        this.url = url;
    }

    public void scrape() {
        try {
            open(this.url);
            // Wait for the page to load and display the title
            $("body").shouldBe(visible);

            ElementsCollection links = $$("a[href]");

            for (int i = 0; i < links.size(); i++) {
                String linkText = links.get(i).getText();
                String linkHref = links.get(i).getAttribute("href");
                System.out.println("Link " + (i + 1) + ": " + linkText + " - " + linkHref);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeWebDriver();
        }
    }

}
