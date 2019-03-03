package ru.cdfe.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

import ru.cdfe.deal.game.GameRepository;
import ru.cdfe.deal.game.InMemoryGameRepository;

@SpringBootApplication(exclude = { ErrorMvcAutoConfiguration.class })
@ServletComponentScan
public class DealServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(DealServerApplication.class, args);
	}
	
	@Bean
	public EmbeddedServletContainerCustomizer embeddedContainerSettings() {
		return container -> {
			container.addErrorPages(new ErrorPage("/error"));
		};
	}
	
	@Bean
	public GameRepository gameRepository() {
		return new InMemoryGameRepository();
	}
}
