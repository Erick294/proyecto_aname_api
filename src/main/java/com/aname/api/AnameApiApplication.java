package com.aname.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableTransactionManagement
public class AnameApiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AnameApiApplication.class, args);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*") //
				// Permitir solicitudes desde cualquier origen
				.allowedMethods("*") // Permitir todos los métodos HTTP
				.allowedHeaders("*"); // Permitir todos los encabezados
		// }

	}
}
