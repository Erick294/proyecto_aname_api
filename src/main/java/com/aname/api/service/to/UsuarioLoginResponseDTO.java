package com.aname.api.service.to;

import java.io.Serializable;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

public class UsuarioLoginResponseDTO extends RepresentationModel<UsuarioLoginResponseDTO> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String token;
	private String type = "Bearer";
	private Integer id;
	private String email;
	private Boolean estado;
	private List<String> roles;

	public UsuarioLoginResponseDTO(String token, Integer id, String email, List<String> roles, Boolean estado) {
		this.token = token;
		this.id = id;
		this.email = email;
		this.roles = roles;
		this.estado=estado;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	
	
}
