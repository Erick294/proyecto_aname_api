package com.aname.api.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "usua_email"))
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuarios_usua_id_seq")
	@SequenceGenerator(name = "usuarios_usua_id_seq", sequenceName = "usuarios_usua_id_seq", allocationSize = 1)
	@Column(name = "usua_id")
	private Integer id;

	@Column(name = "usua_apellidos")
	private String apellidos;

	@Column(name = "usua_nombres")
	private String nombres;

	@Column(name = "usua_email")
	private String email;

	@Column(name = "usua_contrasenia")
	private String password;

	@Column(name = "usua_estado")
	private Boolean estado;

	@Column(name = "usua_direccion")
	private String direccion;

	@Column(name = "usua_ciudad")
	private String ciudad;

	@Column(name = "usua_sexo")
	private String sexo;
	
	@Column(name = "usua_socio")
	private Boolean socio;

	@Column(name = "usua_fecha_nacimiento")
	private LocalDateTime fechaNacimiento;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<DocumentoUsuarios> documentos;
	
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<Competidor> competidores;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Rol rol;
	
	
	@ManyToMany(mappedBy = "usuarios")
	private List<AsociacionDeportiva> asociaciones;

	public Usuario() {
		super();
	}

	public Usuario(String apellidos, String nombres, String email, String password, Boolean estado, String direccion,
			String ciudad, String sexo, LocalDateTime fechaNacimiento, Rol rol) {
		super();

		this.apellidos = apellidos;
		this.nombres = nombres;
		this.email = email;
		this.password = password;
		this.estado = estado;
		this.direccion = direccion;
		this.ciudad = ciudad;
		this.sexo = sexo;
		this.fechaNacimiento = fechaNacimiento;
		this.rol = rol;
	}



}
