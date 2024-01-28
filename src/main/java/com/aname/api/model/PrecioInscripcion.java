package com.aname.api.model;

import java.math.BigDecimal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "precios_inscripciones")
@Data
public class PrecioInscripcion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "precios_inscripciones_prin_id_seq")
	@SequenceGenerator(name = "precios_inscripciones_prin_id_seq", sequenceName = "precios_inscripciones_prin_id_seq", allocationSize = 1)
	@Column(name = "prin_id")
	private Integer id;
	
	@Column(name = "prin_costo_socio")
	private BigDecimal costoSocio;

	@Column(name = "prin_costo_no_socio")
	private BigDecimal costoNoSocio;

	@Column(name = "prin_costo_prueba_adicional")
	private BigDecimal costoPruebaAdicional;

	@Column(name = "prin_cuenta_bancaria")
	private String cuentaBancaria;
	
	@Column(name = "prin_titular_cuenta")
	private String titularCuenta;
	
	@Column(name = "prin_institucion_financiera")
	private String institucionFinanciera;
	
	@OneToOne
	@JoinColumn(name = "camp_id")
	private Campeonato campeonato;
	
	
	

}
