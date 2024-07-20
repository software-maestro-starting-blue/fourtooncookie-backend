package com.startingblue.fourtooncookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FourtoonCookieApplication {

	public static void main(String[] args) {
		SpringApplication.run(FourtoonCookieApplication.class, args);
	}

}
