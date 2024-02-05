package com.aname.api.service.to;

import java.io.Serializable;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class UsuarioLoginResponseDTO extends RepresentationModel<UsuarioLoginResponseDTO> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String token;
	private String type = "Bearer";
	private Integer id;
	private String email;
	private Boolean estado;
	private String rol;
	private Boolean socio;
	private Integer idAsociacion;
	
	public UsuarioLoginResponseDTO(String token, Integer id, String email, String rol, Boolean estado, Boolean socio, Integer idAsociacion) {
		this.token = token;
		this.id = id;
		this.email = email;
		this.rol = rol;
		this.estado=estado;
		this.socio=socio;
		this.idAsociacion = idAsociacion;
	}

	
	
}
