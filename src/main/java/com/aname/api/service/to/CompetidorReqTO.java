package com.aname.api.service.to;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CompetidorReqTO extends RepresentationModel<CompetidorReqTO>{

	private String email;
	private Integer idCampeonato;
	private Integer idAsociacionDeportiva;
	private List<Integer> pruebas;
	
	
}
