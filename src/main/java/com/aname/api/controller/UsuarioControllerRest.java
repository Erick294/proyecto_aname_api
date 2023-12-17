package com.aname.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aname.api.model.Rol;
import com.aname.api.model.Usuario;
import com.aname.api.security.JwtTokenUtil;
import com.aname.api.service.IUsuarioService;
import com.aname.api.service.to.UsuarioLoginDTO;
import com.aname.api.service.to.UsuarioLoginResponseDTO;

@RestController
@CrossOrigin
@RequestMapping(path = "/usuario")
public class UsuarioControllerRest {
	
	@Autowired
	private IUsuarioService usuarioServicio;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	

	@PostMapping(path = "/login")
	public ResponseEntity<?> iniciarSesion(@RequestBody UsuarioLoginDTO loginDTO) {
		try {
			Authentication authentication = this.authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String token = jwtTokenUtil.generateToken(userDetails);
			Usuario usuario = this.usuarioServicio.buscarPorEmail(loginDTO.getEmail());
			Collection<Rol> roles = usuario.getRoles();
			List<String> nombresRoles = new ArrayList<String>();

			for (Rol rol : roles) {
				nombresRoles.add(rol.getNombre());
			}

			UsuarioLoginResponseDTO responseDTO = new UsuarioLoginResponseDTO(token, usuario.getId(),
					usuario.getEmail(), nombresRoles, usuario.getEstado());
			return ResponseEntity.ok(responseDTO);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
		}
	}

}
