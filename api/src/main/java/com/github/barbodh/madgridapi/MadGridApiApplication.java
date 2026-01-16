package com.github.barbodh.madgridapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MadGridApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(MadGridApiApplication.class, args);
	}
}
