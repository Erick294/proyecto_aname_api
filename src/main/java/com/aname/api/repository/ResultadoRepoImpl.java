package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Resultado;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional

public class ResultadoRepoImpl implements IResultadoRepo{

    @PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarResultado(Resultado resultado) {
		System.out.println("JPA REPO");
		this.entityManager.persist(resultado);
	}

	@Override
	public Resultado buscarResultado(Integer id) {
		return this.entityManager.find(Resultado.class, id);
	}

	@Override
	public List<Resultado> buscarTodosResultado() {
		
		TypedQuery<Resultado> myQuery = this.entityManager.createQuery("SELECT c FROM Resultado c", Resultado.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarResultado(Resultado resultado) {
		this.entityManager.merge(resultado);
	}

	@Override
	public void eliminarResultado(Integer id) {
		Resultado c = this.entityManager.getReference(Resultado.class, id);
		this.entityManager.remove(c);
	}
    
}
