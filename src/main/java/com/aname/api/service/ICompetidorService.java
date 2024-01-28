package com.aname.api.service;

import java.util.List;

import com.aname.api.service.to.CategoriaTO;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.CompetidorResTO;
import com.aname.api.service.to.DocsCompetidoresDTO;
import com.aname.api.service.to.FichaInscripcionCampTO;
import com.aname.api.service.to.InscripcionDocsReq;
import com.aname.api.service.to.PreciosInscripcionCalcTO;

public interface ICompetidorService {


	String calcularCategoriaUsuario(Integer edad, String genero);

	void registroInicialCompetidor(CompetidorReqTO c);

	List<CompetidorResTO> listaCompetidoresInscritos();

	void confirmarInscripcionCompetidor(Integer id);

	void negarInscripcionCompetidor(Integer id);

	CompetidorReqTO buscarCompetidorID(Integer id);

	List<CompetidorResTO> listaCompetidoresInscritosPorUsuario(String email);

	List<CompetidorResTO> listaCompetidoresInscritosPorCampeonato(Integer idCampeonato);

	List<CompetidorResTO> listaCompetidoresInscritosPorCampeonatoUser(String email, Integer idCampeonato);

	List<CompetidorResTO> listaCompetidoresPorCampeonatoUser(String email);

	List<CompetidorResTO> listaCompetidoresPorUsuario(String email);


	CompetidorResTO competidororCampeonatoUser(String email, Integer idCampeonato);

	FichaInscripcionCampTO obtenerFichaInscripcion(Integer idCompetidor);

	void registrarPago(DocsCompetidoresDTO doc);

	void registrarFichaInscripcion(DocsCompetidoresDTO doc);

}
