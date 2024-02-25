package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aname.api.service.IAsociacionDeportivaService;
import com.aname.api.service.ICampeonatoService;
import com.aname.api.service.ICompetidorService;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.DocsCompetidoresDTO;

@RestController
@CrossOrigin
@RequestMapping(path = "/competidor")
public class CompetidorControllerRest {

	@Autowired
	private ICompetidorService competidorServiceImpl;

	@Autowired
	private IAsociacionDeportivaService asociacionDeportivaService;

	@Autowired
	private ICampeonatoService campeonatoService;

	// PATHS PARA ADM,JUN, ORG Y
	// ATL------------------------------------------------------------------------------------

	/**
	* Método que permite obtener las asociaciones de la base de datos
	* @return Devolver un objeto ResponseEntity con el resultado del proceso de consulta
	*/
	@GetMapping("/asociaciones")
	public ResponseEntity<?> obtenerAsociaciones() {
		try {

			return ResponseEntity.ok(this.asociacionDeportivaService.listarAsociacionesDeportivas());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error a obtener asociaciones: " + e.getMessage());
		}
	}

	/**
	* Método para obtener los datos de ficha de inscripción del competidor
	* @param idCompetidor - Identificador del competidor a consultar
	* @return Devolver un objeto ResponseEntity con el resultado del proceso de la operación
	*/
	@GetMapping("/fichaInscripcion/{idCompetidor}")
	public ResponseEntity<?> obtenerDatosFichaInscripcion(@PathVariable Integer idCompetidor) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.obtenerFichaInscripcion(idCompetidor));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al generar ficha de inscripcion: " + e.getMessage());
		}
	}

	/**
	* Método responsable por obtener los precios del campeonato que corresponda al idCampeonato
	* @param idCampeonato - Identificador del campeonato a obtener
	* @return Retorna un objeto ResponseEntity con el contenido del proceso de la operación
	*/
	@GetMapping("/campeonato/{idCampeonato}/precios")
	public ResponseEntity<?> obtenerPreciosCampeonato(@PathVariable Integer idCampeonato) {
		try {

			return ResponseEntity.ok(this.campeonatoService.obtenerPreciosCampeonato(idCampeonato));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener precios: " + e.getMessage());
		}
	}

	
	/**
	* Método responsable para realizar el registro inicial de un competidor
	* @param competidor - Objeto CompetidorReqTO con los datos que se desea registrar
	* @return Retorna un ResponseEntity con la ruta del proceso de registro de la competencia
	*/
	@PostMapping("/inscripcionInicial")
	public ResponseEntity<?> inscripcionInicial(@RequestBody CompetidorReqTO competidor) {
		try {

			this.competidorServiceImpl.registroInicialCompetidor(competidor);

			return ResponseEntity.ok("Registro de competidor exitoso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar competidor: " + e.getMessage());
		}
	}

	/**
	* Método de registro del pago de los competidores en la base de datos.
	* @param docs - Contenido con los documentos a registrar
	* @return Devolve un objeto HttpResponse con el resultado del proceso de y manejo del error que se produce
	*/
	@PostMapping("/registroPago")
	public ResponseEntity<?> registrarPago(@RequestBody DocsCompetidoresDTO docs) {
		try {

			this.competidorServiceImpl.registrarPago(docs);

			return ResponseEntity.ok("Pago registrado con exíto, espere a la verificación de los documentos");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar pago: " + e.getMessage());
		}
	}
	
	
	/**
	* Método de registro de ficha de inscripción en la base de datos.
	* @param docs - DTO con los documentos a registrar del competidor
	* @return ResponseEntity con el resultado del proceso
	*/
	@PostMapping("/registroFichaInscripcion")
	public ResponseEntity<?> registrarFicha(@RequestBody DocsCompetidoresDTO docs) {
		try {

			this.competidorServiceImpl.registrarFichaInscripcion(docs);

			return ResponseEntity.ok("Ficha registrada con exíto, espere a la verificación de los documentos");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar ficha de inscripcion: " + e.getMessage());
		}
	}
	
	/**
	* Método que permite obtener un competidor por su ID de la base de datos.
	* @param id - Identificador del competidor a buscar.
	* @return Devolver un objeto ResponseEntity con el resultado del proceso de consulta
	*/
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerCompetidorID(@PathVariable Integer id) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.buscarCompetidorID(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidor: " + e.getMessage());
		}
	}

	/**
	* Método que permite obtener los competidores por email de usuario
	* @param email - Identificador email del usuario a buscar
	* @return Retorna un objeto con la información del competidor
	*/
	@GetMapping("/porUsuario/{email}")
	public ResponseEntity<?> obtenerCompetidorPorUsuario(@PathVariable String email) {
		try {
			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresPorUsuario(email));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidor: " + e.getMessage());
		}
	}

	/**
	* Método que permite obtener los competidores por campeonato y usuario
	* @param idCampeonato - Identificador del campeonato a buscar
	* @param email - Correo electrónico del usuario
	* @return Devolver un objeto ResponseEntity con el resultado del proceso de la operación
	*/
	@GetMapping("/campeonato/{idCampeonato}/usuario/{email}")
	public ResponseEntity<?> obtenerCompetidorPorUserCampeonato(@PathVariable Integer idCampeonato,
			@PathVariable String email) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.competidororCampeonatoUser(email, idCampeonato));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

	// PATHS PARA ADM,JUN,
	// ORG--------------------------------------------------------------------------------

	/**
	* Método que permite obtener la lista de competidores inscritos en la base de datos.
	* @return Retorna un objeto con el contenido de la lista de competidores
	*/
	@GetMapping("/inscritos")
	public ResponseEntity<?> listaCompetidoresInscritos() {
		try {

			this.competidorServiceImpl.listaCompetidoresInscritos();

			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresInscritos());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

	/**
	* Método que permite obtener los registros de competidores por usuario.
	* @param email - El email del usuario
	* @return Lista de competidores que tienen el correo electrónico correspondiente
	*/
	@GetMapping("/inscritos/usuario/{email}")
	public ResponseEntity<?> listaCompetidoresInscritosUser(@PathVariable String email) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresInscritosPorUsuario(email));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

	/**
	* Método que permite obtener los inscritos de un campeonato por idCampeonato e idAsociacion
	* @param idCampeonato - Identificador del campeonato a buscar
	* @param idAsociacion - Identificador de la asociación a buscar
	* @return Devolver un objeto ResponseEntity con el resultado del proceso de consultar la lista de inscritos al campeonato
	*/
	@GetMapping("/inscritos/campeonato/{idCampeonato}")
	public ResponseEntity<?> listaCompetidoresInscritosCampeonato(@PathVariable Integer idCampeonato, @RequestParam Integer idAsociacion) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresInscritosPorCampeonato(idCampeonato, idAsociacion));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

	/**
	* Método que permite obtener los competidores inscritos de un campeonato por el usuario e id el campeonato
	* @param idCampeonato - Identificador del campeonato a buscar
	* @param email - Correo electrónico del usuario a buscar
	* @return Devolver un objeto ResponseEntity con la lista de competidores inscritos
	*/
	@GetMapping("/inscritos/campeonato/{idCampeonato}/usuario/{email}")
	public ResponseEntity<?> listaCompetidoresInscritosUserCampeonato(@PathVariable Integer idCampeonato,
			@PathVariable String email) {
		try {

			return ResponseEntity
					.ok(this.competidorServiceImpl.listaCompetidoresInscritosPorCampeonatoUser(email, idCampeonato));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

	/**
	* Método para confirmar una inscripción en la base de datos.
	* @param id - Identificador del competifor
	* @return Retorna un objeto ResponseEntity con este método del proceso
	*/
	@PutMapping("/confirmarInscripcion/{id}")
	public ResponseEntity<?> confirmarIncripcion(@PathVariable Integer id) {
		try {

			this.competidorServiceImpl.confirmarInscripcionCompetidor(id);

			return ResponseEntity.ok("Inscripcion confirmada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al confirmar inscripcion: " + e.getMessage());
		}
	}

	/**
	* Método que permite negar una inscripción del competidor.
	* @param id - Identificador del competidor que desee negar
	* @return Un ResponseEntity con el mensaje de texto informado en caso de error
	*/
	@PutMapping("/negarInscripcion/{id}")
	public ResponseEntity<?> negarInscripcion(@PathVariable Integer id) {
		try {

			this.competidorServiceImpl.negarInscripcionCompetidor(id);

			return ResponseEntity.ok("Inscripcion negada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al negar inscripcion: " + e.getMessage());
		}
	}
	
	/**
	* Método que permite denegar o rechazar el pago de un competidor
	* @param idCompetidor - Identificador del competidor
	* @return Un ResponseEntity del resultado de la operación
	*/
	@PutMapping("/negarPago/{idCompetidor}")
	public ResponseEntity<?> negarPago(@PathVariable Integer idCompetidor) {
		try {

			this.competidorServiceImpl.negarPago(idCompetidor);

			return ResponseEntity.ok("Pago no acpetado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al negar pago: " + e.getMessage());
		}
	}
	
	/**
	* El método que permite confirmar un pago de un competidor
	* @param idCompetidor - Identificador del competidor
	* @return ResponseEntity con el resultado de la operacion
	*/
	@PutMapping("/confirmarPago/{idCompetidor}")
	public ResponseEntity<?> confirmarPago(@PathVariable Integer idCompetidor) {
		try {

			this.competidorServiceImpl.confirmarPago(idCompetidor);

			return ResponseEntity.ok("Pago Aceptado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al negar pago: " + e.getMessage());
		}
	}
	
	/**
	* Método que permite aprobar la ficha de inscripción del competidor
	* @param docs - Contenido con los documentos aprobados
	* @return Respuesta de la información de la ficha aceptada en el sistema
	*/
	@PostMapping("/aprobarInscripcion")
	public ResponseEntity<?> confirmarPago(@RequestBody DocsCompetidoresDTO docs) {
		try {

			this.competidorServiceImpl.aprobarFichaInscripcion(docs);

			return ResponseEntity.ok("Pago Aceptado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al negar pago: " + e.getMessage());
		}
	}

}
