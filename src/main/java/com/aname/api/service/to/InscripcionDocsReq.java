package com.aname.api.service.to;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class InscripcionDocsReq extends RepresentationModel<InscripcionDocsReq>{

	private String email;
	private Integer idCampeonato;
	private DocResponseDTO fichaInscripcion;
	private DocResponseDTO comprobantePago;
}
