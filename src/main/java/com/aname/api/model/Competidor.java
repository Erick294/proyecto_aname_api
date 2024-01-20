package com.aname.api.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "competidores")
@Data
public class Competidor {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competidores_comp_id_seq")
	@SequenceGenerator(name = "competidores_comp_id_seq", sequenceName = "competidores_comp_id_seq", allocationSize = 1)
	@Column(name = "comp_id")
	private Integer id;

	@Column(name = "comp_fecha_inscripcion")
	private LocalDateTime fechaInscripcion;

	@Column(name = "comp_estado_participacion")
	private String estadoParticipacion;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "campeonatos_competidores", joinColumns = @JoinColumn(name = "comp_id"), inverseJoinColumns = @JoinColumn(name = "camp_id"))
	private List<Campeonato> campeonatos;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "competidores_pruebas", joinColumns = @JoinColumn(name = "comp_id"), inverseJoinColumns = @JoinColumn(name = "prue_id"))
	private List<Prueba> pruebas;

	@OneToMany(mappedBy = "competidor", cascade = CascadeType.ALL)
	private List<DocumentoCompetidores> documentos;

	@OneToMany(mappedBy = "competidor", cascade = CascadeType.ALL)
	private List<Resultado> resultados;

	@ManyToOne
	@JoinColumn(name = "usua_id")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "asde_id")
	private AsociacionDeportiva asociacionDeportiva;

}
