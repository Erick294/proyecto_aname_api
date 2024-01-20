package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.PrecioInscripcion;

public interface IPrecioInscripcionRepo {

	
	void insertarPrecioInscripcion(PrecioInscripcion precio);

	PrecioInscripcion buscarPrecioInscripcion(Integer id);

	List<PrecioInscripcion> buscarTodosPrecioInscripcion();

	void actualizarPrecioInscripcion(PrecioInscripcion precio);

	void eliminarPrecioInscripcion(Integer id);

	PrecioInscripcion buscarPreciosPorCampeonato(Integer idCampeonato);
	

}
