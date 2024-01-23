package com.aname.api.service.to;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CompetidorResTO extends RepresentationModel<CompetidorResTO> {
	
	private Integer id;
	private String email;
	private String nombres;
	private String apellidos;
	private LocalDateTime fechaInscripcion;
	private String nombreCampeonato;
	private Integer idCampeonato;
	private String estadoParticipacion;
	private List<DocResponseDTO> documentos;
	private List<PruebaResponseDTO> pruebas;
}
