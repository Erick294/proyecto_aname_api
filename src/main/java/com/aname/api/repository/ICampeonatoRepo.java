package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Campeonato;

public interface ICampeonatoRepo {
	
	void insertarCampeonato(Campeonato campeonato);

	Campeonato buscarCampeonato(Integer id);

	List<Campeonato> buscarTodosCampeonato();

	void actualizarCampeonato(Campeonato campeonato);

	void eliminarCampeonato(Integer id);

	List<Campeonato> buscarCampeonatosDisponibles();
	

}
