package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Competidor;
import com.aname.api.model.DocumentoCompetidores;

public interface ICompetidorRepo {
	
	void insertarCompetidor(Competidor competidor);

	Competidor buscarCompetidor(Integer id);

	List<Competidor> buscarTodosCompetidor();

	void actualizarCompetidor(Competidor competidor);

	void eliminarCompetidor(Integer id);

	Competidor buscarCompetidorPorUserYCamp(String email, Integer idCampeonato);

	List<Competidor> buscarCompetidoresInscritos();

	List<Competidor> buscarCompetidoresInscritosPorUsuario(String email);

	List<Competidor> buscarCompetidoresInscritosPorCampeonato(Integer idCampeonato);

	List<Competidor> buscarCompetidorresInscritosPorUserYCamp(String email, Integer idCampeonato);

	List<DocumentoCompetidores> buscarDocsCompetidoresInscritosPorUsuario(Integer idCompetidor, String email);

	List<DocumentoCompetidores> buscarDocsCompetidoresInscritosPorCampeonatoYUsers(Integer idCampeonato,
			Integer idCompetidor, String email);

	List<DocumentoCompetidores> buscarDocsCompetidoresInscritosPorCampeonato(Integer idCampeonato,
			Integer idCompetidor);

	List<DocumentoCompetidores> buscarDocsCompetidoresInscritos(Integer idCompetidor);

	

}
