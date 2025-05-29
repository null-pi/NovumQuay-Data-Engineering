package com.utils.novumquay.dataeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.utils.novumquay.scrapper.Scrapper;

@SpringBootApplication
public class DataEngineeringApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataEngineeringApplication.class, args);

		// Example usage of Scrapper
		Scrapper scrapper = new Scrapper("https://arxiv.org/");
		scrapper.scrape();
		System.out.println("Scraping completed.");
	}
}
