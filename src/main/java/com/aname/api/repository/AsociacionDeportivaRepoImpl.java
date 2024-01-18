package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.AsociacionDeportiva;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class AsociacionDeportivaRepoImpl implements IAsociacionDeportivaRepo{

    @PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarAsociacionDeportiva(AsociacionDeportiva asociacionDeportiva) {
		System.out.println("JPA REPO");
		this.entityManager.persist(asociacionDeportiva);
	}

	@Override
	public AsociacionDeportiva buscarAsociacionDeportiva(Integer id) {
		return this.entityManager.find(AsociacionDeportiva.class, id);
	}

	@Override
	public List<AsociacionDeportiva> buscarTodosAsociacionDeportiva() {
		
		TypedQuery<AsociacionDeportiva> myQuery = this.entityManager.createQuery("SELECT c FROM AsociacionDeportiva c", AsociacionDeportiva.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarAsociacionDeportiva(AsociacionDeportiva asociacionDeportiva) {
		this.entityManager.merge(asociacionDeportiva);
	}

	@Override
	public void eliminarAsociacionDeportiva(Integer id) {
		AsociacionDeportiva c = this.entityManager.getReference(AsociacionDeportiva.class, id);
		this.entityManager.remove(c);
	}
    
}
