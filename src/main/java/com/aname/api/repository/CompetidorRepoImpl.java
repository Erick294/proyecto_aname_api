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

	@Override
	public void insertarCompetidor(Competidor competidor) {
		// System.out.println("JPA REPO");
		this.entityManager.persist(competidor);
	}

	@Override
	public Competidor buscarCompetidor(Integer id) {
		return this.entityManager.find(Competidor.class, id);
	}

	@Override
	public List<Competidor> buscarTodosCompetidor() {

		TypedQuery<Competidor> myQuery = this.entityManager.createQuery("SELECT c FROM Competidor c", Competidor.class);
		return myQuery.getResultList();
	}

	@Override
	public Competidor buscarCompetidorPorUserYCamp(String email, Integer idCampeonato) {
		TypedQuery<Competidor> myQuery = this.entityManager
				.createQuery("SELECT c FROM Competidor c JOIN c.usuario u JOIN c.campeonatos ca "
						+ "WHERE u.email = :email " + "AND ca.id = :idCampeonato", Competidor.class);

		myQuery.setParameter("email", email);
		myQuery.setParameter("idCampeonato", idCampeonato);

		return myQuery.getResultList().get(0);
	}

	@Override
	public List<Competidor> buscarCompetidorPorUsuario(String email) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
				"SELECT c FROM Competidor c JOIN c.usuario u " + "WHERE u.email = :email ", Competidor.class);

		myQuery.setParameter("email", email);

		return myQuery.getResultList();
	}

	// Todos los competidores inscritos
	@Override
	public List<Competidor> buscarCompetidoresInscritos() {
		TypedQuery<Competidor> myQuery = this.entityManager
				.createQuery("SELECT c FROM Competidor c WHERE c.estadoParticipacion=:estado", Competidor.class);
		myQuery.setParameter("estado", "Inscrito");
		return myQuery.getResultList();
	}



	// Competidores inscriptos por
	// campeonato--------------------------------------------------------------------------

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


	// Competidores inscritos por usuario y por
	// campeonato---------------------------------------------------------------

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
	
	
	//competidores todos los estads por usuario y por campeonato ----------------------------
	
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



	@Override
	public List<DocumentoCompetidores> buscarDocsCompetidores(Integer idCompetidor) {
		TypedQuery<DocumentoCompetidores> myQuery = this.entityManager.createQuery(
				"SELECT d FROM DocumentoCompetidores d JOIN d.competidor c WHERE c.id=:idCompetidor",
				DocumentoCompetidores.class);

		myQuery.setParameter("idCompetidor", idCompetidor);

		return myQuery.getResultList();
	}

	// Competidores inscritos por
	// usuario----------------------------------------------------------------------------------
	@Override
	public List<Competidor> buscarCompetidoresInscritosPorUsuario(String email) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery("SELECT c FROM Competidor c JOIN c.usuario u "
				+ "WHERE c.estadoParticipacion=:estado " + "AND u.email = :email", Competidor.class);

		myQuery.setParameter("estado", "Inscrito");
		myQuery.setParameter("email", email);

		return myQuery.getResultList();
	}

	

	// **************************************************************************************************************

	@Override
	public void actualizarCompetidor(Competidor competidor) {
		this.entityManager.merge(competidor);
	}

	@Override
	public void eliminarCompetidor(Integer id) {
		Competidor c = this.entityManager.getReference(Competidor.class, id);
		this.entityManager.remove(c);
	}

}
