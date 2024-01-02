package com.aname.api.service.to;

import java.time.LocalDateTime;

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

	



	public DocResponseDTO getDocumentoIdentidad() {
		return documentoIdentidad;
	}





	public void setDocumentoIdentidad(DocResponseDTO documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}





	public DocResponseDTO getFotografia() {
		return fotografia;
	}





	public void setFotografia(DocResponseDTO fotografia) {
		this.fotografia = fotografia;
	}





	public UsuarioRegistroDTO() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public LocalDateTime getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDateTime fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

}
