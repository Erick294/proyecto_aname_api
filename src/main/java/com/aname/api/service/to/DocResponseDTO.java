package com.aname.api.service.to;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class DocResponseDTO extends RepresentationModel<DocResponseDTO> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String nombre;
	private String username;
	private String link;
	private String extension;
	

}
