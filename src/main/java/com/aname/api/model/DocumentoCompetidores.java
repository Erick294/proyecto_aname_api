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
@Table(name = "documentos_competidores")
@Data
public class DocumentoCompetidores {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documentos_competidores_doco_id_seq")
	@SequenceGenerator(name = "documentos_competidores_doco_id_seq", sequenceName = "documentos_competidores_doco_id_seq", allocationSize = 1)
	@Column(name = "doco_id")
	private Integer id;

	@Column(name = "doco_nombre")
	private String nombre;

	@Column(name = "doco_link")
	private String link;

	@Column(name = "doco_extension")
	private String extension;

	@ManyToOne
	@JoinColumn(name = "comp_id")
	private Competidor competidor;

	

}
