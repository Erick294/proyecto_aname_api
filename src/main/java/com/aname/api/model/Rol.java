package com.aname.api.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol implements GrantedAuthority{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rol")
	@SequenceGenerator(name = "seq_rol", sequenceName = "seq_rol", allocationSize = 1)
	@Column(name = "rol_id")
	private Integer id;

	@Column(name = "rol_codigo")
	private String codigo;

	@Column(name = "rol_descripcion")
	private String descripcion;
	

	@ManyToMany(mappedBy = "roles")
	private List<Usuario> usuarios;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String getAuthority() {
		return this.codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

}