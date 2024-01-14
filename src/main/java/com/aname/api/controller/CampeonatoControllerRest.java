package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	
	

}
