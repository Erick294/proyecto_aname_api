package com.aname.api.service;

import java.util.List;

import com.aname.api.model.Campeonato;
import com.aname.api.service.to.CampeonatoReqDTO;

public interface ICampeonatoService {

	void registrarCampeonato(CampeonatoReqDTO campeonato);

	List<CampeonatoReqDTO> listarCampeonatos();

	List<CampeonatoReqDTO> listarCampeonatosDisponibles();

	Campeonato buscarCampeonatoPorID(Integer id);
}
