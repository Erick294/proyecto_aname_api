package com.aname.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aname.api.service.IUsuarioService;

@Configuration
public class WebSecurityConfig {

	@Autowired
	TokenUtils jwtUtils;

	@Autowired
	IUsuarioService userDetailsService;

	@Autowired
	JWTAuthorizationFilter authorizationFilter;

	/**
	* Configura el filtro de seguridad para el logeo de usuarios
	* @param httpSecurity - Objeto HTTP Security para validar ciertas configuraciones
	* @param authenticationManager - El administrador de autenticación
	* @return SecurityFilterChain a utilizar para las cuestiones de seguridad del sistema
	*/
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager)
			throws Exception {

		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtUtils, userDetailsService);
		jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
		jwtAuthenticationFilter.setFilterProcessesUrl("/login");

		return httpSecurity.csrf(config -> config.disable()).authorizeHttpRequests(auth -> {
			auth.requestMatchers("/login").permitAll();
			auth.requestMatchers("/usuario/aprobarRegistroUsuario/**", 
					"/usuario/negarRegistroUsuario/**",
					"/usuario/aprobarUsuarioAsociado/**",
					"/usuario/negarUsuarioAsociado/**",
					"/usuario/asociacion/**")
			.hasAnyAuthority("ORG", "JUN", "ADM");
			
			auth.requestMatchers("/usuario/costo/**").hasAnyAuthority("ORG", "JUN", "ADM", "ATL");
			auth.requestMatchers("/usuario/**").permitAll();
			
			auth.requestMatchers("/files/**").permitAll();
			auth.requestMatchers("/campeonato/disponibles", "/campeonato/{id:[0-9]+}/pruebas").hasAnyAuthority("ORG", "JUN", "ADM", "ATL");
			auth.requestMatchers("/campeonato/**").hasAnyAuthority("ORG", "JUN", "ADM");
			auth.requestMatchers("/campeonato/pruebas").hasAnyAuthority("ORG", "JUN", "ADM", "ATL");
			
			auth.requestMatchers("/competidor/inscritos/**", 
					"/competidor/confirmarInscripcion/**", 
					"/competidor/negarInscripcion/**", 
					"/competidor/negarPago/**",
					"/competidor/confirmarPago/**",
					"/competidor/aprobarInscripcion/**")
			.hasAnyAuthority("ORG", "JUN", "ADM");
			auth.requestMatchers("/competidor/asociaciones/**").permitAll();
			auth.requestMatchers("/competidor/**").hasAnyAuthority("ORG", "JUN", "ADM", "ATL");
			
			auth.anyRequest().authenticated();
		}).sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}).addFilter(jwtAuthenticationFilter)
				.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	/**
	* Crear e instancia de PasswordEncoder para codificar las contraseñas de los usuarios
	* @return una instancia de BCryptPasswordEncoder
	*/
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	* Crea un Administrador de autenticación para usar con Jakarta
	* @param httpSecurity - El HttpSecurity para utilizar para la autenticación
	* @param passwordEncoder - La entidad para codificar la contraseña
	* @return Un administrador de autenticación que se puede utilizar para autenticar solicitudes
	*/
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
			throws Exception {
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder).and().build();
	}

}
