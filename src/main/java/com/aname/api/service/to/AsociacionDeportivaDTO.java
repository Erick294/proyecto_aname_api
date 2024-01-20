package com.aname.api.service.to;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class AsociacionDeportivaDTO extends RepresentationModel<AsociacionDeportivaDTO> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;

}
