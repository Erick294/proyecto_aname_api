package com.aname.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Competidor;

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
	    TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
	        "SELECT c FROM Competidor c JOIN c.usuario u JOIN c.campeonatos ca " +
	        "WHERE u.email = :email AND ca.id = :idCampeonato",
	        Competidor.class
	    );

	    myQuery.setParameter("email", email);
	    myQuery.setParameter("idCampeonato", idCampeonato);

	    return myQuery.getResultList().get(0);
	}

	
	@Override
	public List<Competidor> buscarCompetidoresInscritos() {
	    TypedQuery<Competidor> myQuery = this.entityManager.createQuery(
	        "SELECT c FROM Competidor c WHERE c.estadoParticipacion=:estado",
	        Competidor.class
	    );

	    myQuery.setParameter("estado", "Inscrito");

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
