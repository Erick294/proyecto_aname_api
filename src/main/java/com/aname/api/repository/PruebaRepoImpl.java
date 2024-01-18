package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Campeonato;
import com.aname.api.model.Prueba;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class PruebaRepoImpl implements IPruebaRepo {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarPrueba(Prueba prueba) {
		this.entityManager.persist(prueba);
	}

	@Override
	public Prueba buscarPrueba(Integer id) {
		return this.entityManager.find(Prueba.class, id);
	}

	@Override
	public List<Prueba> buscarTodosPrueba() {
		TypedQuery<Prueba> myQuery = this.entityManager.createQuery("SELECT p FROM Prueba p", Prueba.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarPrueba(Prueba prueba) {
		this.entityManager.merge(prueba);
	}

	@Override
	public void eliminarPrueba(Integer id) {
		Prueba p = this.entityManager.getReference(Prueba.class, id);
		this.entityManager.remove(p);
	}

}
