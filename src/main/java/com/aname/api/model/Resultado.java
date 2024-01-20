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
@Table(name = "resultados")
@Data
public class Resultado {

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resultados_resu_id_seq")
	@SequenceGenerator(name = "resultados_resu_id_seq", sequenceName = "resultados_resu_id_seq", allocationSize = 1)
	@Column(name = "resu_id")
	private Integer id;
	
	@Column(name = "resu_intento")
	private Integer intento;

    @Column(name = "resu_marca")
	private String marca;

    @Column(name = "resu_medida")
	private String medida;

    @Column(name = "resu_unidad")
	private String unidad;

    @ManyToOne
	@JoinColumn(name = "prue_id")
 	private Prueba prueba;
    
    @ManyToOne
   	@JoinColumn(name = "comp_id")
    private Competidor competidor;
    
}
