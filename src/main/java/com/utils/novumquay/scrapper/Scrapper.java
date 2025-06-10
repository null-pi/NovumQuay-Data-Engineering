package com.utils.novumquay.scrapper;

import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.ElementsCollection;
import static com.codeborne.selenide.Condition.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Scrapper {
    @Value("${scraper.target.url:https://arxiv.org/}")
    private String url;

    @PostConstruct
    public void init() {
        log.info("Initializing Scrapper service with URL: {}", url);
    }

    public void scrape() {
        // The try-finally block is moved to the ScrapingTask to control the lifecycle
        log.info("Starting scraping process for URL: {}", url);
        
        open(this.url);
        
        // Wait for the page to load and display the title
        $("body").shouldBe(visible);
        log.info("Page body is visible. Scraping links...");

        ElementsCollection links = $$("a[href]");
        log.info("Found {} links on the page.", links.size());

        for (int i = 0; i < links.size(); i++) {
            String linkText = links.get(i).getText();
            String linkHref = links.get(i).getAttribute("href");
            log.info("Link {}: {} - {}", (i + 1), linkText, linkHref);
        }
        
        log.info("Link scraping completed successfully");
    }

    /**
     * Closes the WebDriver. This is called by the main task runner when the
     * application is ready to shut down.
     */
    public void shutdown() {
        log.info("Closing WebDriver...");
        closeWebDriver();
        log.info("WebDriver closed.");
    }
}
