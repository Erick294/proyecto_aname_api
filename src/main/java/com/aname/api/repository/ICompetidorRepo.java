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


	List<Competidor> buscarCompetidorresInscritosPorUserYCamp(String email, Integer idCampeonato);

	List<Competidor> buscarCompetidorPorUsuario(String email);

	List<DocumentoCompetidores> buscarDocsCompetidores(Integer idCompetidor);

	Competidor buscarCompetidoresPorUserYCamp(String email, Integer idCampeonato);

	List<Competidor> buscarCompetidoresInscritosPorCampeonato(Integer idCampeonato, Integer idAsociacion);

}
