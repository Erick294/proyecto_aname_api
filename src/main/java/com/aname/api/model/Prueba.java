package com.aname.api.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pruebas")
@Data
public class Prueba {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pruebas_prue_id_seq")
	@SequenceGenerator(name = "pruebas_prue_id_seq", sequenceName = "pruebas_prue_id_seq", allocationSize = 1)
	@Column(name = "prue_id")
	private Integer id;
	
	@Column(name = "prue_nombre")
	private String nombre;
	
	@Column(name = "prue_intentos")
	private Integer intentos;
	
	@Column(name = "prue_tipo")
	private String tipo;
	
	@ManyToMany(mappedBy = "pruebas")
	private List<Campeonato> campeonatos;

}
