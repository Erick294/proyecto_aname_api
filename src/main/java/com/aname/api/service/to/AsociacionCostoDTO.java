package com.aname.api.service.to;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class AsociacionCostoDTO extends RepresentationModel<AsociacionCostoDTO>{
	
	private Integer id;
	private String nombre;
	private BigDecimal costo;
	private String numeroCuenta;
	private String titular;
	private String institucionFinanciera;

}
