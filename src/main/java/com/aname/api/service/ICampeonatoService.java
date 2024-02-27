package com.aname.api.service;

import java.util.List;

import com.aname.api.model.Campeonato;
import com.aname.api.service.to.CampeonatoReqDTO;
import com.aname.api.service.to.DTOReporte1;
import com.aname.api.service.to.DTOReporte2;
import com.aname.api.service.to.PreciosCampeonatosTO;

public interface ICampeonatoService {

	void registrarCampeonato(CampeonatoReqDTO campeonato);

	List<CampeonatoReqDTO> listarCampeonatos();

	List<CampeonatoReqDTO> listarCampeonatosDisponibles();

	Campeonato buscarCampeonatoPorID(Integer id);

	PreciosCampeonatosTO obtenerPreciosCampeonato(Integer idCampeonato);

	List<DTOReporte1> reporteUno();
	 List<DTOReporte2> reporteDos();
}
