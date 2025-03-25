package br.com.meetime.hubspot.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "br.com.meetime.hubspot")
@EnableJpaRepositories(basePackages = "br.com.meetime.hubspot.repository")
@EntityScan(basePackages = "br.com.meetime.hubspot.model")
public class HubspotApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubspotApplication.class, args);
	}

}
