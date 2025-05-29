package com.utils.novumquay.scrapper;

import org.springframework.stereotype.Component;

import com.codeborne.selenide.Configuration;
import com.utils.novumquay.utils.PathChecker;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SelenideConfig {

    @Value("${chrome.binary.path:/usr/bin/google-chrome}")
    private String chromeBinaryPath;

    @Value("${scraper.base.dir:/app}")
    private String scraperBaseDir;

    @PostConstruct
    public void configureSelenide() {
        try {
            log.info("Configuring Selenide with Chrome binary path: {}", chromeBinaryPath);
            log.info("Scraper base directory: {}", scraperBaseDir);

            // Set the system properties for Selenide
            // System.setProperty("selenide.browser", "chrome");
            // System.setProperty("selenide.chrome.binary", chromeBinaryPath);
            // System.setProperty("selenide.reportsFolder", scraperBaseDir + "/reports");
            // System.setProperty("selenide.downloadsFolder", scraperBaseDir +
            // "/downloads");

            ChromeOptions chromeOptions = new ChromeOptions();

            String userDataDir = scraperBaseDir + "/chrome-user-data/session-" + System.nanoTime();
            chromeOptions.addArguments("--user-data-dir=" + userDataDir);

            log.info("Using Chrome user data directory: {}", userDataDir);

            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--proxy-server=direct://");
            chromeOptions.addArguments("--proxy-bypass-list=*");
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("--disable-web-security");
            chromeOptions.addArguments("--allow-running-insecure-content");

            String reportsFolder = scraperBaseDir + "/reports";
            PathChecker reportsPathChecker = new PathChecker(reportsFolder, "dir");
            reportsPathChecker.ensurePathExists();
            log.info("Setting reports folder: {}", reportsFolder);

            String downloadsFolder = scraperBaseDir + "/downloads";
            PathChecker downloadsPathChecker = new PathChecker(downloadsFolder, "dir");
            downloadsPathChecker.ensurePathExists();
            log.info("Setting downloads folder: {}", downloadsFolder);

            HashMap<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("download.default_directory", downloadsFolder);
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.directory_upgrade", true);
            chromePrefs.put("safebrowsing.enabled", "true");
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("plugins.always_open_pdf_externally", true);

            chromeOptions.setExperimentalOption("prefs", chromePrefs);

            chromeOptions.setBinary(chromeBinaryPath);

            Configuration.browserCapabilities = chromeOptions;

            Configuration.reportsFolder = reportsFolder;
            Configuration.downloadsFolder = downloadsFolder;
            Configuration.timeout = 10000; // 10 seconds
            Configuration.pageLoadTimeout = 60000; // 60 seconds
            Configuration.browserSize = "1920x1080";

            log.info("Selenide configured successfully with the following settings:");
        } catch (Exception e) {
            log.error("Error configuring Selenide: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure Selenide", e);
        } finally {
            log.info("Selenide configuration completed successfully.");
        }
    }
}
