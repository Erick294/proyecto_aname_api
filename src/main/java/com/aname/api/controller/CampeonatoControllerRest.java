package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aname.api.service.IAsociacionDeportivaService;
import com.aname.api.service.ICampeonatoService;
import com.aname.api.service.IPruebaService;
import com.aname.api.service.to.CampeonatoReqDTO;

@RestController
@CrossOrigin
@RequestMapping(path = "/campeonato")
public class CampeonatoControllerRest {

	@Autowired
	private ICampeonatoService campeonatoService;

	@Autowired
	private IPruebaService pruebaService;

	@Autowired
	private IAsociacionDeportivaService asociacionDeportivaService;

	// PATHS PARA ADM,JUN,
	// ORG------------------------------------------------------------------------------------

	@PostMapping
	public ResponseEntity<?> registrarCampeonato(@RequestBody CampeonatoReqDTO campeonatoReqDTO) {
		try {

			this.campeonatoService.registrarCampeonato(campeonatoReqDTO);
			return ResponseEntity.ok("Campeonato Registrado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar campeonato: " + e.getMessage());
		}
	}

	@GetMapping("/pruebas")
	public ResponseEntity<?> obtenerListaPruebas() {
		try {

			return ResponseEntity.ok(this.pruebaService.listarPruebas());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar pruebas: " + e.getMessage());
		}
	}

	@GetMapping("/sedes")
	public ResponseEntity<?> obtenerListaSedes() {
		try {

			return ResponseEntity.ok(this.asociacionDeportivaService.listarAsociacionesDeportivas());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar sedes: " + e.getMessage());
		}
	}

	@GetMapping()
	public ResponseEntity<?> obtenerListaCampeonatos() {
		try {

			return ResponseEntity.ok(this.campeonatoService.listarCampeonatos());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar campeonatos: " + e.getMessage());
		}
	}

	// PATHS PARA ADM,JUN, ORG Y
	// ATL------------------------------------------------------------------------------------

	@GetMapping("/disponibles")
	public ResponseEntity<?> obtenerListaCampeonatosDisponibles() {
		try {

			return ResponseEntity.ok(this.campeonatoService.listarCampeonatosDisponibles());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar campeonatos: " + e.getMessage());
		}
	}

	@GetMapping("/{id}/pruebas")
	public ResponseEntity<?> obtenerListaPruebas(@PathVariable Integer id) {
		try {

			return ResponseEntity.ok(this.pruebaService.listarPruebasPorCampeonato(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar pruebas: " + e.getMessage());
		}
	}

}
