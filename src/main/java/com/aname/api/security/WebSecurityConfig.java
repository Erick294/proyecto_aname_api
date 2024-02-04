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
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	@Autowired
	TokenUtils jwtUtils;

	@Autowired
	IUsuarioService userDetailsService;

	@Autowired
	JWTAuthorizationFilter authorizationFilter;

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
					"aprobarUsuarioAsociado/**",
					"/negarUsuarioAsociado/**")
			.hasAnyAuthority("ORG", "JUN", "ADM");
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

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
			throws Exception {
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder).and().build();
	}

}
