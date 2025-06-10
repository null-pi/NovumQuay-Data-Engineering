package com.utils.novumquay.dataeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.utils.novumquay.dataeng", "com.utils.novumquay.scrapper"})
public class DataEngineeringApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataEngineeringApplication.class, args);
	}
}
