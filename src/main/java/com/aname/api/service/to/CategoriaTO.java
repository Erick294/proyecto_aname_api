package com.aname.api.service.to;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CategoriaTO extends RepresentationModel<CategoriaTO>{
	
	private Integer id;
	
	private String nombre;

}
