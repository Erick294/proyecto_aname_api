package com.aname.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aname.api.service.IRolService;
import com.aname.api.service.IUsuarioService;
import com.aname.api.service.to.UsuarioRegistroDTO;

@RestController
@CrossOrigin
@RequestMapping(path = "/usuario")
public class UsuarioControllerRest {
	
	@Autowired
	private IUsuarioService usuarioServicio;
	
	@Autowired
	private IRolService rolService;

	@PostMapping()
	public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRegistroDTO registroDTO) {
		System.out.println(registroDTO.getEmail());
		try {
			if (usuarioServicio.existeNombreUsuario(registroDTO.getEmail())) {
				return ResponseEntity.badRequest().body("Ya existe un usuario con el mismo nombre de usuario.");
			}
			UsuarioRegistroDTO usuario = usuarioServicio.guardar(registroDTO);
			return ResponseEntity.ok(usuario);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar usuario: " + e.getMessage());
		}
	}
	
	
	@GetMapping(path = "/roles", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<String> getPerfiles() {
		return this.rolService.buscarTodosRol();
	}

	
}
