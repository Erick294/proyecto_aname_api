package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.PrecioInscripcion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class PrecioInscripcionRepoImpl implements IPrecioInscripcionRepo{

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarPrecioInscripcion(PrecioInscripcion precio) {
		this.entityManager.persist(precio);
	}

	@Override
	public PrecioInscripcion buscarPrecioInscripcion(Integer id) {
		return this.entityManager.find(PrecioInscripcion.class, id);
	}

	@Override
	public List<PrecioInscripcion> buscarTodosPrecioInscripcion() {
		TypedQuery<PrecioInscripcion> myQuery = this.entityManager.createQuery("SELECT p FROM PrecioInscripcion p", PrecioInscripcion.class);
		return myQuery.getResultList();
	}
	
	@Override
	public PrecioInscripcion buscarPreciosPorCampeonato(Integer idCampeonato) {
	    TypedQuery<PrecioInscripcion> myQuery = this.entityManager.createQuery(
	        "SELECT p FROM PrecioInscripcion p JOIN p.campeonato c WHERE c.id = :idCampeonato",
	        PrecioInscripcion.class
	    );

	    myQuery.setParameter("idCampeonato", idCampeonato);

	    return myQuery.getSingleResult();
	}

	@Override
	public void actualizarPrecioInscripcion(PrecioInscripcion precio) {
		this.entityManager.merge(precio);
	}

	@Override
	public void eliminarPrecioInscripcion(Integer id) {
		PrecioInscripcion p = this.entityManager.getReference(PrecioInscripcion.class, id);
		this.entityManager.remove(p);
	}

}
