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
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categorias_cate_id_seq")
	@SequenceGenerator(name = "categorias_cate_id_seq", sequenceName = "categorias_cate_id_seq", allocationSize = 1)
	@Column(name = "cate_id")
	private Integer id;
	
	@Column(name = "cate_nombre")
	private String nombre;

    @Column(name = "cate_edad_minima")
	private Integer edadMinima;

    @Column(name = "cate_edad_maxima")
	private Integer edadMaxima;
    
    @Column(name = "cate_genero")
    private String genero;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
	        name = "categorias_pruebas",
	        joinColumns = @JoinColumn(name = "cate_id"),
	        inverseJoinColumns = @JoinColumn(name = "prue_id")
	    )
	private List<Prueba> pruebas;
    
}
