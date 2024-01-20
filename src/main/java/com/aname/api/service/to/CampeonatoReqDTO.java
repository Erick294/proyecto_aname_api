package com.aname.api.service.to;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CampeonatoReqDTO extends RepresentationModel<CampeonatoReqDTO> implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nombre;
	private String organizador;
	private String sede;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private LocalDateTime inscripcionInicio;
	private LocalDateTime inscripcionFin;
	private List<Integer> pruebas;
	private BigDecimal costoSocio;
	private BigDecimal costoNoSocio;
	private BigDecimal costoPruebaAdicional;
	private String cuentaBancaria;


}
