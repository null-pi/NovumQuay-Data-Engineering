package com.utils.novumquay.scrapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScrapingTask implements CommandLineRunner {

    private final Scrapper scrapper;
    // private final ApplicationContext appContext;

    public ScrapingTask(Scrapper scrapper, ApplicationContext appContext) {
        this.scrapper = scrapper;
        // this.appContext = appContext;
    }

    @Override
    public void run(String... args) throws Exception {
        // int exitCode = 0;
        try {
            log.info("CommandLineRunner started. Beginning the scraping task...");
            scrapper.scrape();
            log.info("Scraping task completed successfully.");
        } catch (Exception e) {
            log.error("Scraping task failed.", e);
            // exitCode = 1; // Set a non-zero exit code on failure
        } finally {
            scrapper.shutdown();
            
            // // This final variable can be safely captured by the lambda expression below.
            // final int finalExitCode = exitCode;
            // log.info("Shutting down the application with exit code: {}", finalExitCode);
            
            // // This cleanly closes the Spring application context with the correct exit code.
            // SpringApplication.exit(appContext, () -> finalExitCode);
        }
    }
}
