package com.aname.api.service;

import java.util.List;

import com.aname.api.service.to.CategoriaTO;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.CompetidorResTO;
import com.aname.api.service.to.FichaInscripcionCampTO;
import com.aname.api.service.to.InscripcionDocsReq;
import com.aname.api.service.to.PreciosInscripcionCalcTO;

public interface ICompetidorService {

	FichaInscripcionCampTO obtenerFichaInscripcion(String email, Integer idcampeonato);

	CategoriaTO calcularCategoriaUsuario(Integer edad, String genero);


	void registroInicialCompetidor(CompetidorReqTO c);

	PreciosInscripcionCalcTO calcularPreciosInscripcion(Integer idCampeonato, String email, List<String> pruebas);

	void inscripcionCompleta(InscripcionDocsReq i);

	List<CompetidorResTO> listaCompetidoresInscritos();

	void confirmarInscripcionCompetidor(Integer id);

	void negarInscripcionCompetidor(Integer id);

	List<CompetidorResTO> listaCompetidoresInscritosPorUsuario(String email);

	List<CompetidorResTO> listaCompetidoresInscritosPorCampeonato(Integer idCampeonato);

	List<CompetidorResTO> listaCompetidoresInscritosPorCampeonatoUser(String email, Integer idCampeonato);

}