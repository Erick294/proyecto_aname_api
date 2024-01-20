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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "campeonatos")
@Data
public class Campeonato {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campeonatos_camp_id_seq")
	@SequenceGenerator(name = "campeonatos_camp_id_seq", sequenceName = "campeonatos_camp_id_seq", allocationSize = 1)
	@Column(name = "camp_id")
	private Integer id;

	@Column(name = "camp_nombre")
	private String nombre;

	@Column(name = "camp_organizador")
	private String organizador;

	@Column(name = "camp_sede")
	private String sede;

	@Column(name = "camp_fecha_inicio")
	private LocalDateTime fechaInicio;

	@Column(name = "camp_fecha_fin")
	private LocalDateTime fechaFin;

	@Column(name = "camp_inscripcion_fin")
	private LocalDateTime inscripcionFin;

	@Column(name = "camp_inscripcion_inicio")
	private LocalDateTime inscripcionInicio;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "campeonatos_pruebas", joinColumns = @JoinColumn(name = "camp_id"), inverseJoinColumns = @JoinColumn(name = "prue_id"))
	private List<Prueba> pruebas;

	@ManyToMany(mappedBy = "campeonatos")
	private List<Competidor> competidores;

	@ManyToMany(mappedBy = "campeonatos")
	private List<AsociacionDeportiva> asociaciones;

	@OneToOne(mappedBy = "campeonato", cascade = CascadeType.ALL)
	private PrecioInscripcion precioInscripcion;
}
