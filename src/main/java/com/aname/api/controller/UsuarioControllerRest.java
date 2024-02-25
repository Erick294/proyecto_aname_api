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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aname.api.service.IRolService;
import com.aname.api.service.IUsuarioService;
import com.aname.api.service.email_service.IEmailService;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.UsuarioRegistroDTO;
import com.aname.api.service.to.email_DTO.EmailDTO;
import com.aname.api.service.to.email_DTO.EmailFileDTO;
import com.aname.api.service.to.email_DTO.EmailHTMLDTO;

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

	/**
	* Método de registro de un usuario en la base de datos. También registra la nueva instancia de UsuarioRegistroDTO
	* @param registroDTO - Objeto que contiene los datos que se desea registrar
	* @return Devuelve un objeto ResponseEntity con la respuesta a la operacion
	*/
	@PostMapping()
	public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRegistroDTO registroDTO) {
		System.out.println(registroDTO.getEmail());
		try {
			// Método para existir nombre de usuario con el mismo nombre de usuario.
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
	
	/**
	* Método que busca un la asociacion deportiva de un usuario por su email en la base de datos.
	* @param email - Email del usuario que se desea buscar
	* @return Retorna un objeto ResponseEntity con el contenido
	*/
	@GetMapping("/{email}/idAsociacion")
	public ResponseEntity<?> buscarAsociacionPorUsuario(@PathVariable String email) {
		try {
			
			Integer idAsociacion = this.usuarioServicio.buscarIDAsociacionUsuario(email);
			return ResponseEntity.ok(idAsociacion);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar asociacion del usuario: " + e.getMessage());
		}
	}
	
	/**
	* Método de búsqueda del costo de una asociación en la base de datos.
	* @param idAsociacion - Identificador de la asociación a buscar.
	* @return Regresa un objeto contenido o con el mensaje de respuesta
	*/
	@GetMapping("/costo/asociacion/{idAsociacion}")
	public ResponseEntity<?> buscarCostoAsociacion(@PathVariable Integer idAsociacion) {
		try {
			return ResponseEntity.ok(this.usuarioServicio.buscarCostoAsociacion(idAsociacion));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar costo de asociacion: " + e.getMessage());
		}
	}

	/**
	* Método responsable por aprobación del registro de usuario
	* @param email - Email del usuario a aprobar
	* @return ResponseEntity con el codigo de estado de la operacion
	*/
	@PutMapping("/aprobarRegistroUsuario/{email}")
	public ResponseEntity<?> aprobrarRegistroUsuario(@PathVariable String email) {

		try {

			this.usuarioServicio.aprobrarRegistroUsuario(email);
			return ResponseEntity.ok("Registro de usuario aprobado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al aprobar usuario: " + e.getMessage());
		}
	}
	
	/**
	* Método que permite negar un registro de usuario en la base de datos
	* @param email - Email del registro de usuario
	* @return Retorna un ResponseEntity con estado del resultado del proceso HTTP (200) o Json con el error que se produce
	*/
	@PutMapping("/negarRegistroUsuario/{email}")
	public ResponseEntity<?> negarRegistroUsuario(@PathVariable String email) {

		try {

			this.usuarioServicio.negarRegistroUsuario(email);
			return ResponseEntity.ok("Registro de usuario denegado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al denegar usuario: " + e.getMessage());
		}
	}
	
	/**
	* Registra un usuario en la base de datos que tiene el pago de asociación
	* @param u - Objeto con los documentos del pago realizado
	* @return Con el resultado del proceso de registro del pago de la asociación o con el error que se produzca
	*/
	@PostMapping("/registroPagoAsociacion")
	public ResponseEntity<?> registrarPagoAsociacion(@RequestBody DocResponseDTO u) {

		try {

			this.usuarioServicio.registrarPagoAsociacion(u);
			return ResponseEntity.ok("Registro exitoso de pago de asociación");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar el pago de usuario: " + e.getMessage());
		}
	}
	
	/**
	* Método responsable de aprobar el registro de un usuario que cuente con una asociacion deportiva
	* @param email - Email del usuario
	* @return Retorna un objeto ResponseEntity con el resultado del proceso
	*/
	@PutMapping("/aprobarUsuarioAsociado/{email}")
	public ResponseEntity<?> aprobarUsuarioAsociado(@PathVariable String email) {

		try {

			this.usuarioServicio.aprobarUsuarioAsociado(email);
			return ResponseEntity.ok("Registro de socio denegado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al denegar socio: " + e.getMessage());
		}
	}
	
	/**
	* Método que permite negar un registro de usuario asociado en la base de datos
	* @param email - Email del usuario
	* @return Resultado del proceso de gestión de las cuentas de registro
	*/
	@PutMapping("/negarUsuarioAsociado/{email}")
	public ResponseEntity<?> negarUsuarioAsociado(@PathVariable String email) {

		try {

			this.usuarioServicio.negarUsuarioAsociado(email);
			return ResponseEntity.ok("Registro de socio denegado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al denegar socio: " + e.getMessage());
		}
	}
	
	/**
	* Método de búsqueda de usuarios por asociación
	* @param idAsociacion - Identificador del asociación a buscar
	* @return Devolver un objeto ResponseEntity con el resultado del proceso de búsqueda
	*/
	@GetMapping("/asociacion/{idAsociacion}")
	public ResponseEntity<?> buscarUsuariosPorAsociacion(@PathVariable Integer idAsociacion) {

		try {
	
			return ResponseEntity.ok(this.usuarioServicio.listarUsuariosRegistradosPorAsociacion(idAsociacion));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar usuarios: " + e.getMessage());
		}
	}
	
	
	/**
	* Método de búsqueda de todos los roles del sistema
	* @return Lista todos los roles del sistema
	*/
	@GetMapping(path = "/roles", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<String> getPerfiles() {
		return this.rolService.buscarTodosRol();
	}

	/**
	* Método de envío de un mensaje de correo simple (Solo texto)
	* @param emailDTO - Objeto con los datos del correo electrónico a enviar
	* @return ResponseEntity con el estado del mensaje
	*/
	@PostMapping("/email/enviarSimple")
	public ResponseEntity<?> enviarSimpleEmail(@RequestBody EmailDTO emailDTO) {

		System.out.println("Mensaje Recibido " + emailDTO);

		emailService.sendSimpleEmail(emailDTO.getToUser(), emailDTO.getSubject(), emailDTO.getMessage());

		Map<String, String> response = new HashMap<>();
		response.put("estado", "Enviado");

		return ResponseEntity.ok(response);
	}

	/**
	* Método de envío de un archivo de correo electrónico. Para el formulario de mensajes: creado por parámetro
	* @param emailFileDTO - Objeto que contiene los datos del archivo de correo electrónico
	* @return ResponseEntity con el estado del mensaje
	*/
	@PostMapping("/email/enviarArchivo")
	public ResponseEntity<?> enviarEmailArchivo(@ModelAttribute EmailFileDTO emailFileDTO) {

		try {
			String fileName = emailFileDTO.getFile().getOriginalFilename();

			Path path = Paths.get("src/mail/resources/files/" + fileName);

			Files.createDirectories(path.getParent());
			Files.copy(emailFileDTO.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			File file = path.toFile();

			emailService.sendEmailArchivo(emailFileDTO.getToUser(), emailFileDTO.getSubject(),
					emailFileDTO.getMessage(), file);

			Map<String, String> response = new HashMap<>();
			response.put("estado", "Enviado");
			response.put("archivo", fileName);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			throw new RuntimeException("Error al enviar el Email con el archivo. " + e.getMessage());
		}
	}
	
	 /**
	 * Método de envío de un correo electrónico.
	 * @param request - Objeto que contiene el requerimiento a enviar
	 * @return ResponseEntity con el estado del mensaje
	 */
	 @PostMapping("/email/enviarHTML")
	    public ResponseEntity<String> sendEmail(@RequestBody EmailHTMLDTO request) {
	        try {
	            emailService.sendHtmlEmail(request.getToUser(), request.getSubject(), request.getHtmlContent());
	            return ResponseEntity.ok("Email enviado correctamente");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo electrónico: " + e.getMessage());
	        }
	    }

}
