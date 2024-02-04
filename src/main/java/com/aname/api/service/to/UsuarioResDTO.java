package com.aname.api.service.to;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class UsuarioResDTO extends RepresentationModel<UsuarioResDTO> {

	private Integer id;
	private String nombres;
	private String apellidos;
	private String email;
	private String rol;
	private Boolean estado;
	private String direccion;
	private String sexo;
	private LocalDateTime fechaNacimiento;
	private String ciudad;
	private Integer idAsociacion;
	private Boolean socio;
	



	
}
