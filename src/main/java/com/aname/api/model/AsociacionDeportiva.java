package com.aname.api.model;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "asociaciones_deportivas")
@Data
public class AsociacionDeportiva {

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asociaciones_deportivas_asde_id_seq")
	@SequenceGenerator(name = "asociaciones_deportivas_asde_id_seq", sequenceName = "asociaciones_deportivas_asde_id_seq", allocationSize = 1)
	@Column(name = "asde_id")
	private Integer id;
	
	@Column(name = "asde_nombre")
	private String nombre;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
	        name = "asociaciones_deportivas_usuarios",
	        joinColumns = @JoinColumn(name = "asde_id"),
	        inverseJoinColumns = @JoinColumn(name = "usua_id")
	    )
	private List<Usuario> usuarios;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
	        name = "asociaciones_deportivas_campeonatos",
	        joinColumns = @JoinColumn(name = "asde_id"),
	        inverseJoinColumns = @JoinColumn(name = "camp_id")
	    )
	private List<Campeonato> campeonatos;
    
}