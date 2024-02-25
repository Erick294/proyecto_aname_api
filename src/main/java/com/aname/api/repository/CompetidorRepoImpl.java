package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Competidor;
import com.aname.api.model.DocumentoCompetidores;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class CompetidorRepoImpl implements ICompetidorRepo {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	* Este método se utiliza para insertar un nuevo competidor en la base de datos
	* @param competidor - Competidor a insertar en la base de datos
	*/
	@Override
	public void insertarCompetidor(Competidor competidor) {
		this.entityManager.persist(competidor);
	}

	/**
	* Buscar un competidor en la base de datos
	* @param id - Id del Competidor que se desea buscar
	* @return Devuelve el Competidor si existe o no devuelve nulo en caso contrario
	*/
	@Override
	public Competidor buscarCompetidor(Integer id) {
		return this.entityManager.find(Competidor.class, id);
	}

	/**
	* Busca todas las competencias de la base de datos
	* @return Lista de competencias que estan en la base de datos
	*/
	@Override
	public List<Competidor> buscarTodosCompetidor() {

		TypedQuery<Competidor> myQuery = this.entityManager.createQuery("SELECT c FROM Competidor c", Competidor.class);
		return myQuery.getResultList();
	}

	/**
	* Busca Competidor por email de usuario e id del campeonato
	* @param email - Email del Competidor
	* @param idCampeonato - Id del Campeonato
	* @return Retorna un objeto Competidor de ser encontrado o nulo en caso contrario
	*/
	@Override
	public Competidor buscarCompetidorPorUserYCamp(String email, Integer idCampeonato) {
		TypedQuery<Competidor> myQuery = this.entityManager
				.createQuery("SELECT c FROM Competidor c JOIN c.usuario u JOIN c.campeonatos ca "
						+ "WHERE u.email = :email " + "AND ca.id = :idCampeonato", Competidor.class);

		myQuery.setParameter("email", email);
		myQuery.setParameter("idCampeonato", idCampeonato);

		return myQuery.getResultList().get(0);
	}

	/**
	* Busca todos los competidores por email de usuario
	* @param email - Email del competidor
	* @return Lista de competidores que contienen el email a buscar
	*/
	@Override
	public List<Competidor> buscarCompetidorPorUsuario(String email) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
				"SELECT c FROM Competidor c JOIN c.usuario u " + "WHERE u.email = :email ", Competidor.class);

		myQuery.setParameter("email", email);

		return myQuery.getResultList();
	}

	// Todos los competidores inscritos

	/**
	* Busca todos los competidores inscritos
	* @return Lista de competidores inscritos en la base de datos
	*/
	@Override
	public List<Competidor> buscarCompetidoresInscritos() {
		TypedQuery<Competidor> myQuery = this.entityManager
				.createQuery("SELECT c FROM Competidor c WHERE c.estadoParticipacion=:estado", Competidor.class);
		myQuery.setParameter("estado", "Inscrito");
		return myQuery.getResultList();
	}

	// Competidores inscriptos por campeonato--------------------------------------------------------------------------

	/**
	* Busca los competidores inscritos por campeonato y asosación deportiva. (con estados: Preinscrito, Pago Aceptado y Pago Denegado)
	* @param idCampeonato - Identificador del campeonato a buscar
	* @param idAsociacion - Identificador de la asociacion deportiva a buscar
	* @return Lista de competidores encontrados o nulo
	*/
	@Override
	public List<Competidor> buscarCompetidoresInscritosPorCampeonato(Integer idCampeonato, Integer idAsociacion) {
		TypedQuery<Competidor> myQuery = this.entityManager
				.createQuery("SELECT c FROM Competidor c JOIN c.campeonatos ca JOIN c.asociacionDeportiva a "
						+ "WHERE (c.estadoParticipacion=:estado1 OR c.estadoParticipacion=:estado2 "
						+ "OR c.estadoParticipacion=:estado3) " 
						+ "AND ca.id = :idCampeonato "
						+ "AND a.id =:idAsociacion", Competidor.class);

		myQuery.setParameter("estado1", "Preinscrito");
		myQuery.setParameter("estado2", "Pago Aceptado");
		myQuery.setParameter("estado3", "Pago Denegado");
		myQuery.setParameter("idCampeonato", idCampeonato);

		myQuery.setParameter("idAsociacion", idAsociacion);

		return myQuery.getResultList();
	}


	// Competidores inscritos por usuario y por campeonato---------------------------------------------------------------

	/**
	* Busca competidores inscritos por el email de usuario y el id del campeonato
	* @param email - Email del competidor
	* @param idCampeonato - Id del campeonato
	* @return Lista de competidores enocntrados o nulo
	*/
	@Override
	public List<Competidor> buscarCompetidorresInscritosPorUserYCamp(String email, Integer idCampeonato) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
				"SELECT c FROM Competidor c JOIN c.usuario u JOIN c.campeonatos ca "
						+ "WHERE c.estadoParticipacion=:estado AND u.email = :email AND ca.id = :idCampeonato",
				Competidor.class);

		myQuery.setParameter("email", email);
		myQuery.setParameter("idCampeonato", idCampeonato);
		myQuery.setParameter("estado", "Inscrito");

		return myQuery.getResultList();
	}
	
	
	//competidores todos los estados por usuario y por campeonato ----------------------------
	
	/**
	* Buscar competidores por email de usuario e id del campeonato
	* @param email - Email del competidor
	* @param idCampeonato - Id del campeonato a buscar
	* @return Objeto Competidor encontrado o nulo
	*/
	@Override
	public Competidor buscarCompetidoresPorUserYCamp(String email, Integer idCampeonato) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
				"SELECT c FROM Competidor c JOIN c.usuario u JOIN c.campeonatos ca "
						+ "WHERE u.email = :email AND ca.id = :idCampeonato",
				Competidor.class);

		myQuery.setParameter("email", email);
		myQuery.setParameter("idCampeonato", idCampeonato);

		return myQuery.getResultList().get(0);
	}
	
	
	///Documentos de competidores

	/**
	* Buscar todos los documentos de un competidor
	* @param idCompetidor - Id del competidor
	* @return Lista de documentos del competidor buscado
	*/
	@Override
	public List<DocumentoCompetidores> buscarDocsCompetidores(Integer idCompetidor) {
		TypedQuery<DocumentoCompetidores> myQuery = this.entityManager.createQuery(
				"SELECT d FROM DocumentoCompetidores d JOIN d.competidor c WHERE c.id=:idCompetidor",
				DocumentoCompetidores.class);

		myQuery.setParameter("idCompetidor", idCompetidor);

		return myQuery.getResultList();
	}

	// Competidores inscritos por usuario----------------------------------------------------------------------------------
	
	/**
	* Busca todos los competidores inscritos por email de usuario
	* @param email - Email del competidor a buscar
	* @return Lista de competidores inscritos con el email buscado
	*/
	@Override
	public List<Competidor> buscarCompetidoresInscritosPorUsuario(String email) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery("SELECT c FROM Competidor c JOIN c.usuario u "
				+ "WHERE c.estadoParticipacion=:estado " + "AND u.email = :email", Competidor.class);

		myQuery.setParameter("estado", "Inscrito");
		myQuery.setParameter("email", email);

		return myQuery.getResultList();
	}

	

	// **************************************************************************************************************

	/**
	* Actualiza un Competidor en la base de datos
	* @param competidor - Competidor a actualizar en la base de datos
	*/
	@Override
	public void actualizarCompetidor(Competidor competidor) {
		this.entityManager.merge(competidor);
	}

	/**
	* Eliminación de un competidor en la base de datos
	* @param id - Id del competidor a ser eliminado
	*/
	@Override
	public void eliminarCompetidor(Integer id) {
		Competidor c = this.entityManager.getReference(Competidor.class, id);
		this.entityManager.remove(c);
	}

}
