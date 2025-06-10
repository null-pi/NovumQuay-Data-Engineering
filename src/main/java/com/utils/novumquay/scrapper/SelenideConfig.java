package com.utils.novumquay.scrapper;

import org.springframework.stereotype.Component;
import com.codeborne.selenide.Configuration;
import com.utils.novumquay.utils.PathChecker; // Assuming this utility class exists and works as intended
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SelenideConfig {

    // Non-static fields to allow Spring's @Value injection
    @Value("${chrome.binary.path:/usr/bin/google-chrome}")
    private String chromeBinaryPath;

    @Value("${scraper.base.dir:/app}")
    private String scraperBaseDir;

    // Non-static PostConstruct method, run after dependency injection
    @PostConstruct
    public void configureSelenide() {
        try {
            log.info("Configuring Selenide with Chrome binary path: {}", chromeBinaryPath);
            log.info("Scraper base directory: {}", scraperBaseDir);

            // This ensures the base directory for user data and reports exists
            new PathChecker(scraperBaseDir, "dir").ensurePathExists();

            ChromeOptions chromeOptions = new ChromeOptions();

            // This path will now be correctly constructed with the injected value
            String userDataDir = scraperBaseDir + "/chrome-user-data/session-" + System.nanoTime();
            chromeOptions.addArguments("--user-data-dir=" + userDataDir);
            log.info("Using Chrome user data directory: {}", userDataDir);

            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--window-size=1920,1080");
            
            // Explicitly point to the VNC display set in your Dockerfile
            chromeOptions.addArguments("--display=:1");

            // Simplified network settings
            chromeOptions.addArguments("--proxy-server='direct://'");
            chromeOptions.addArguments("--proxy-bypass-list=*");
            chromeOptions.addArguments("--ignore-certificate-errors");

            String reportsFolder = scraperBaseDir + "/reports";
            new PathChecker(reportsFolder, "dir").ensurePathExists();
            log.info("Setting reports folder: {}", reportsFolder);

            String downloadsFolder = scraperBaseDir + "/downloads";
            new PathChecker(downloadsFolder, "dir").ensurePathExists();
            log.info("Setting downloads folder: {}", downloadsFolder);

            HashMap<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("download.default_directory", downloadsFolder);
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.directory_upgrade", true);
            chromePrefs.put("safebrowsing.enabled", true);
            chromePrefs.put("plugins.always_open_pdf_externally", true);

            chromeOptions.setExperimentalOption("prefs", chromePrefs);
            chromeOptions.setBinary(chromeBinaryPath);

            Configuration.browserCapabilities = chromeOptions;
            Configuration.reportsFolder = reportsFolder;
            Configuration.downloadsFolder = downloadsFolder;
            Configuration.timeout = 10000;
            Configuration.pageLoadTimeout = 60000;
            Configuration.browserSize = "1920x1080";

            log.info("Selenide configuration completed successfully.");
        } catch (Exception e) {
            log.error("Fatal error configuring Selenide: {}", e.getMessage(), e);
            // Fail fast if configuration fails, preventing the app from running in a broken state
            throw new RuntimeException("Failed to configure Selenide", e);
        }
    }
}
