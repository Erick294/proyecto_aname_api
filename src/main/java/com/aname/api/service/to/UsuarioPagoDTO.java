package com.aname.api.service.to;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class UsuarioPagoDTO extends RepresentationModel<UsuarioPagoDTO>{
	
	private String email;
	private DocResponseDTO documentoPagoAsociacion;

}
