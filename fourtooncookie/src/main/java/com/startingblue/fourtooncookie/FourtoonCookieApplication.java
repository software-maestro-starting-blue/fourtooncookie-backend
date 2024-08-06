package com.startingblue.fourtooncookie;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class FourtoonCookieApplication {

	public static void main(String[] args) {
		Hashtag.validateAllHashtags();
		SpringApplication.run(FourtoonCookieApplication.class, args);
	}

}
