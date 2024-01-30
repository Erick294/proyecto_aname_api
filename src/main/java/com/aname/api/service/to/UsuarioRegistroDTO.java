package com.aname.api.service.to;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UsuarioRegistroDTO {

	private Integer id;
	private String nombres;
	private String apellidos;
	private String email;
	private String password;
	private String rol;
	private Boolean estado;
	private String direccion;
	private String sexo;
	private LocalDateTime fechaNacimiento;
	private String ciudad;
	private DocResponseDTO documentoIdentidad;
	private DocResponseDTO fotografia;
	private Integer idAsociacion;

	public UsuarioRegistroDTO() {
		
	}

	public UsuarioRegistroDTO(Integer id, String nombres, String apellidos, String email, String password, String rol,
			Boolean estado, String direccion, String sexo, LocalDateTime fechaNacimiento, String ciudad) {
		super();
		this.id = id;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
		this.rol = rol;
		this.estado = estado;
		this.direccion = direccion;
		this.sexo = sexo;
		this.fechaNacimiento = fechaNacimiento;
		this.ciudad = ciudad;
	}

	
}
