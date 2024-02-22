package com.aname.api.model;

import java.math.BigDecimal;

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
@Table(name = "precios_asociaciones")
@Data
public class PrecioAsociacion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "precios_asociaciones_pras_id_seq")
	@SequenceGenerator(name = "precios_asociaciones_pras_id_seq", sequenceName = "precios_asociaciones_pras_id_seq", allocationSize = 1)
	@Column(name = "pras_id")
	private Integer id;
	
	@Column(name = "pras_costo")
	private BigDecimal costo;

	@Column(name = "pras_cuenta_bancaria")
	private String cuentaBancaria;
	
	@Column(name = "pras_titular_cuenta")
	private String titularCuenta;
	
	@Column(name = "pras_institucion_financiera")
	private String institucionFinanciera;
	
	@OneToOne
	@JoinColumn(name = "asde_id")
	private AsociacionDeportiva asociacion;
	
	
}
