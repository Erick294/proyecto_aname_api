package com.aname.api.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aname.api.model.Usuario;
import com.aname.api.service.IUsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private TokenUtils jwtUtils;

	@Autowired
	private IUsuarioService userDetailsService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String tokenHeader = request.getHeader("Authorization");

		if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
			String token = tokenHeader.substring(7);
			String email = jwtUtils.getUsernameFromToken(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			Usuario usuario = userDetailsService.buscarUsuarioPorEmail(email);

			System.out.println(usuario.getEstado());
			if (jwtUtils.isTokenValid(token, userDetails) && usuario.getEstado()) {

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
						null, userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
