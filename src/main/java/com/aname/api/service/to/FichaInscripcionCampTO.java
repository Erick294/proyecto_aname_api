package com.aname.api.service.to;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class FichaInscripcionCampTO extends RepresentationModel<FichaInscripcionCampTO> {

	private String nombres;
	private String apellidos;
	private String direccion;
	private String ciudad;
	private String email;
	private String sexo;
	private List<Integer> asociacionesUsuariosIds;
	private LocalDateTime fechaNacimiento;
	private CategoriaTO categoria;
	private List<PruebaResponseDTO> pruebas;
	private BigDecimal costoSocio;
	private BigDecimal costoNoSocio;
	private BigDecimal costoPruebaAdicional;
	private String cuentaBancaria;

}
