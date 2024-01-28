package com.aname.api.service.to;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class FichaInscripcionCampTO extends RepresentationModel<FichaInscripcionCampTO> {

	//nombreCampeonato
	//fecha de campeonato
	//asociacion nombre
	private String nombreCampeonato;
	private String fechaCampeonato;
	private String asociacion;
	private String nombres;
	private String apellidos;
	private String direccion;
	private String ciudad;
	private String email;
	private String sexo;
	private LocalDateTime fechaNacimiento;
	private String categoria;
	private List<PruebaResponseDTO> pruebas;
	
}
