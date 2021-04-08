package ru.zakrzhevskiy.lighthouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.zakrzhevskiy.lighthouse.service.storage.StorageStrategy;

@SpringBootApplication
@EnableJpaAuditing
public class LighthouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(LighthouseApplication.class, args);
	}

	@Autowired
	private ApplicationContext context;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("GET", "POST", "PUT", "DELETE")
						.allowedOrigins("*")
						.allowedHeaders("*");
			}
		};
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public StorageStrategy storageStrategy(@Value("${storage.strategy.hosting}") String qualifier) {
		return (StorageStrategy) context.getBean(qualifier);
	}

}
