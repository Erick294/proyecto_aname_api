package com.aname.api.service;

import java.util.List;

import com.aname.api.model.Prueba;
import com.aname.api.service.to.PruebaResponseDTO;

public interface IPruebaService {
	
	List<PruebaResponseDTO> listarPruebas();

	Prueba convertirAPruebaResponseDTO(PruebaResponseDTO p);

	PruebaResponseDTO convertirPruebaResponseDTO(Prueba p);

	void actualizarPrueba(Prueba prueba);

	Prueba buscarPrueba(Integer S);

	List<PruebaResponseDTO> listarPruebasPorCampeonato(Integer idCampeonato);

	List<PruebaResponseDTO> listarPruebasPorCampeonatoYCategoria(Integer idCampeonato, Integer idCategoria);
	

}
