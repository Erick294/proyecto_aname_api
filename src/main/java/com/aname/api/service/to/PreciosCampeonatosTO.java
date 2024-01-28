package com.aname.api.service.to;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class PreciosCampeonatosTO extends RepresentationModel<PreciosCampeonatosTO> {
	
	private BigDecimal costoSocio;

	private BigDecimal costoNoSocio;

	private BigDecimal costoPruebaAdicional;

	private String cuentaBancaria;
	
	private String titularCuenta;
	
	private String institucionFinanciera;
}
