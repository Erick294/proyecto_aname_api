package com.aname.api.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aname.api.service.IRolService;
import com.aname.api.service.IUsuarioService;
import com.aname.api.service.email_service.IEmailService;
import com.aname.api.service.to.UsuarioRegistroDTO;
import com.aname.api.service.to.email_DTO.EmailDTO;
import com.aname.api.service.to.email_DTO.EmailFileDTO;

@RestController
@CrossOrigin
@RequestMapping(path = "/usuario")
public class UsuarioControllerRest {
	
	@Autowired
	private IUsuarioService usuarioServicio;

	@Autowired
    private IEmailService emailService;
	
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

	@PostMapping("/email/enviarSimple")
    public ResponseEntity<?> enviarSimpleEmail(@RequestBody EmailDTO emailDTO){

        System.out.println("Mensaje Recibido " + emailDTO);

        emailService.sendSimpleEmail(emailDTO.getToUser(), emailDTO.getSubject(), emailDTO.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("estado", "Enviado");

        return ResponseEntity.ok(response);
    }


    @PostMapping("/email/enviarArchivo")
    public ResponseEntity<?> enviarEmailArchivo(@ModelAttribute EmailFileDTO emailFileDTO){

        try {
            String fileName = emailFileDTO.getFile().getOriginalFilename();

            Path path = Paths.get("src/mail/resources/files/" + fileName);

            Files.createDirectories(path.getParent());
            Files.copy(emailFileDTO.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            File file = path.toFile();

            emailService.sendEmailArchivo(emailFileDTO.getToUser(), emailFileDTO.getSubject(), emailFileDTO.getMessage(), file);

            Map<String, String> response = new HashMap<>();
            response.put("estado", "Enviado");
            response.put("archivo", fileName);

            return ResponseEntity.ok(response);

        } catch (Exception e){
            throw new RuntimeException("Error al enviar el Email con el archivo. " + e.getMessage());
        }
    }

	
}
