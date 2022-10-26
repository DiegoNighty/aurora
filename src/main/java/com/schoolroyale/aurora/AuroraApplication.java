package com.schoolroyale.aurora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@SpringBootApplication
public class AuroraApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuroraApplication.class, args);
	}

}
