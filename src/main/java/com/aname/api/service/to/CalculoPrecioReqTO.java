package com.aname.api.service.to;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CalculoPrecioReqTO extends RepresentationModel<CalculoPrecioReqTO>{
	
	private Integer idCampeonato;
	private String email;
	private Integer pruebasAdicionales;
	private List<String> pruebas;

}
