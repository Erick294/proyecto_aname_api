package com.aname.api.service.to;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
@Data
public class PreciosInscripcionCalcTO extends RepresentationModel<PreciosInscripcionCalcTO>{

	private BigDecimal precioSocio;
	private BigDecimal precioNoSocio;
	private BigDecimal precioPruebasAdicionales;
	private BigDecimal precioPentatlon;
	private BigDecimal total;
}
