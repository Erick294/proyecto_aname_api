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
		System.out.println("JPA REPO");
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
						+ "WHERE u.email = :email AND ca.id = :idCampeonato", Competidor.class);

		myQuery.setParameter("email", email);
		myQuery.setParameter("idCampeonato", idCampeonato);

		return myQuery.getResultList().get(0);
	}

	@Override
	public List<Competidor> buscarCompetidoresInscritos() {
		TypedQuery<Competidor> myQuery = this.entityManager
				.createQuery("SELECT c FROM Competidor c WHERE c.estadoParticipacion=:estado", Competidor.class);

		myQuery.setParameter("estado", "Inscrito");

		return myQuery.getResultList();
	}

	@Override
	public List<Competidor> buscarCompetidoresInscritosPorCampeonato(Integer idCampeonato) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
				"SELECT c FROM Competidor c JOIN c.campeonatos ca WHERE c.estadoParticipacion=:estado AND ca.id = :idCampeonato",
				Competidor.class);

		myQuery.setParameter("estado", "Inscrito");
		myQuery.setParameter("idCampeonato", idCampeonato);

		return myQuery.getResultList();
	}

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

	@Override
	public List<Competidor> buscarCompetidoresInscritosPorUsuario(String email) {
		TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
				"SELECT c FROM Competidor c JOIN c.usuario u WHERE c.estadoParticipacion=:estado AND u.email = :email",
				Competidor.class);

		myQuery.setParameter("estado", "Inscrito");
		myQuery.setParameter("email", email);

		return myQuery.getResultList();
	}

	@Override
	public List<DocumentoCompetidores> buscarDocsCompetidoresInscritosPorUsuario(String email) {
		TypedQuery<DocumentoCompetidores> myQuery = this.entityManager.createQuery(
				"SELECT d FROM DocumentoCompetidores d JOIN d.competidor c JOIN c.usuario u WHERE c.estadoParticipacion = :estado AND u.email = :email",
				DocumentoCompetidores.class);

		myQuery.setParameter("estado", "Inscrito");
		myQuery.setParameter("email", email);

		return myQuery.getResultList();
	}

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
