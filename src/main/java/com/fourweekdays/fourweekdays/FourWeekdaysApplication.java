package com.fourweekdays.fourweekdays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FourWeekdaysApplication {

	public static void main(String[] args) {
		SpringApplication.run(FourWeekdaysApplication.class, args);
	}

}
