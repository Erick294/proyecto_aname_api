package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Campeonato;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class CampeonatoRepoImpl implements ICampeonatoRepo {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarCampeonato(Campeonato campeonato) {
		System.out.println("JPA REPO");
		this.entityManager.persist(campeonato);
	}

	@Override
	public Campeonato buscarCampeonato(Integer id) {
		return this.entityManager.find(Campeonato.class, id);
	}

	@Override
	public List<Campeonato> buscarTodosCampeonato() {
		
		TypedQuery<Campeonato> myQuery = this.entityManager.createQuery("SELECT c FROM Campeonato c", Campeonato.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarCampeonato(Campeonato campeonato) {
		this.entityManager.merge(campeonato);
	}

	@Override
	public void eliminarCampeonato(Integer id) {
		Campeonato c = this.entityManager.getReference(Campeonato.class, id);
		this.entityManager.remove(c);
	}

}
