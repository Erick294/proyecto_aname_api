package com.aname.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "documentos_usuarios")
@Data
public class DocumentoUsuarios {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documentos_usuario_dous_id_seq")
	@SequenceGenerator(name = "documentos_usuario_dous_id_seq", sequenceName = "documentos_usuario_dous_id_seq", allocationSize = 1)
	@Column(name = "dous_id")
	private Integer id;

	@Column(name = "dous_nombre")
	private String nombre;

	@Column(name = "dous_link")
	private String link;

	@Column(name = "dous_extension")
	private String extension;

	@ManyToOne
	@JoinColumn(name = "usua_id")
	private Usuario usuario;

	

}
