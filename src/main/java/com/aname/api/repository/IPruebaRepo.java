package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Prueba;

public interface IPruebaRepo {
	
	void insertarPrueba(Prueba prueba);

	Prueba buscarPrueba(Integer id);

	List<Prueba> buscarTodosPrueba();

	void actualizarPrueba(Prueba prueba);

	void eliminarPrueba(Integer id);

	List<Prueba> buscarPruebasPorCampeonato(Integer idCampeonato);

	List<Prueba> buscarPruebasPorCampeonatoYCategoria(Integer idCampeonato, Integer idCategoria);
	

}
