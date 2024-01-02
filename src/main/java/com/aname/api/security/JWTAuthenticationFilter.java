package com.aname.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aname.api.model.Rol;
import com.aname.api.model.Usuario;
import com.aname.api.service.IUsuarioService;
import com.aname.api.service.to.UsuarioLoginResponseDTO;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private TokenUtils jwtUtils;

	private IUsuarioService usuarioService;

	public JWTAuthenticationFilter(TokenUtils jwtUtils, IUsuarioService usuarioService) {
		this.jwtUtils = jwtUtils;
		this.usuarioService = usuarioService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		Usuario userEntity = null;
		String email = "";
		String password = "";
		try {
			userEntity = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
			email = userEntity.getEmail();
			password = userEntity.getPassword();
		} catch (StreamReadException e) {
			throw new RuntimeException(e);
		} catch (DatabindException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
				password);

		return getAuthenticationManager().authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		User user = (User) authResult.getPrincipal();
		System.out.println("User: " + user.getUsername());
		String token = jwtUtils.generateAccesToken(user.getUsername());

		response.addHeader("Authorization", token);

		Map<String, Object> httpResponse = new HashMap<>();
		Usuario usuario = this.usuarioService.buscarUsuarioPorEmail(user.getUsername());
		Rol rol = usuario.getRol();
		String nombreRol = rol.getCodigo();


		UsuarioLoginResponseDTO userResponse = new UsuarioLoginResponseDTO(token, usuario.getId(), usuario.getEmail(),
				nombreRol, usuario.getEstado());

		httpResponse.put("Usuario", userResponse);
		
//        httpResponse.put("token", token);
//        httpResponse.put("Message", "Autenticacion Correcta");
//        httpResponse.put("Username", user.getUsername());
//        httpResponse.put("roles", httpResponse);

		response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().flush();

		super.successfulAuthentication(request, response, chain, authResult);
	}

}
