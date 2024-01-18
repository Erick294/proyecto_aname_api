package com.aname.api.service.to;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class PruebaResponseDTO extends RepresentationModel<PruebaResponseDTO> implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String nombre;
	
	private Integer intentos;
	
	private String tipo; 
	
	

}
