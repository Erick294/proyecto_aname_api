package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	// PATHS PARA ADM,JUN, ORG Y
	// ATL------------------------------------------------------------------------------------
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
	
	

}
