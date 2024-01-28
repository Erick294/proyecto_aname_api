package com.aname.api.service.to;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class DocsCompetidoresDTO extends RepresentationModel<DocsCompetidoresDTO> {
	
	private Integer idCompetidor;
	private String nombre;
	private String link;
	private String extension;
	
}
