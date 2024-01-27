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
import com.aname.api.service.ICompetidorService;
import com.aname.api.service.to.CalculoPrecioReqTO;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.InscripcionDocsReq;

@RestController
@CrossOrigin
@RequestMapping(path = "/competidor")
public class CompetidorControllerRest {

	@Autowired
	private ICompetidorService competidorServiceImpl;

	@Autowired
	private IAsociacionDeportivaService asociacionDeportivaService;

	// PATHS PARA ADM,JUN, ORG Y
	// ATL------------------------------------------------------------------------------------

	@GetMapping("/asociaciones")
	public ResponseEntity<?> obtenerAsociaciones() {
		try {

			return ResponseEntity.ok(this.asociacionDeportivaService.listarAsociacionesDeportivas());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error a obtener asociaciones: " + e.getMessage());
		}
	}

	@GetMapping("/fichaInscripcion")
	public ResponseEntity<?> obtenerDatosFichaInscripcion(@RequestParam String email,
			@RequestParam Integer idCampeonato) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.obtenerFichaInscripcion(email, idCampeonato));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al generar ficha de inscripcion: " + e.getMessage());
		}
	}

	@GetMapping("/calcularPrecios")
	public ResponseEntity<?> calcularPreciosInscripcion(@RequestBody CalculoPrecioReqTO calculo) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.calcularPreciosInscripcion(calculo.getIdCampeonato(),
					calculo.getEmail(), calculo.getPruebas()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al calcular precios: " + e.getMessage());
		}
	}

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

	@PostMapping("/inscripcionCompleta")
	public ResponseEntity<?> inscripcionCompleta(@RequestBody InscripcionDocsReq inscripcion) {
		try {

			this.competidorServiceImpl.inscripcionCompleta(inscripcion);

			return ResponseEntity.ok("Competidor inscrito, por favor espere a la verificación de los documentos");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al inscribir competidor: " + e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerCompetidorID(@PathVariable Integer id) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.buscarCompetidorID(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidor: " + e.getMessage());
		}
	}

	@GetMapping("/porUsuario/{email}")
	public ResponseEntity<?> obtenerCompetidorPorUsuario(@PathVariable String email) {
		try {
			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresPorUsuario(email));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidor: " + e.getMessage());
		}
	}

	// PATHS PARA ADM,JUN,
	// ORG--------------------------------------------------------------------------------
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

	@GetMapping("/inscritos/usuario/{email}")
	public ResponseEntity<?> listaCompetidoresInscritosUser(@PathVariable String email) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresInscritosPorUsuario(email));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

	@GetMapping("/inscritos/campeonato/{idCampeonato}")
	public ResponseEntity<?> listaCompetidoresInscritosCampeonato(@PathVariable Integer idCampeonato) {
		try {

			return ResponseEntity.ok(this.competidorServiceImpl.listaCompetidoresInscritosPorCampeonato(idCampeonato));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener competidores: " + e.getMessage());
		}
	}

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

}
