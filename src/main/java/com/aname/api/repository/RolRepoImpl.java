package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Rol;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Transactional
@Repository
public class RolRepoImpl implements IRolRepo{
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarRol(Rol Rol) {
		this.entityManager.persist(Rol);
	}


	@Override
	public List<Rol> buscarTodosRol() {
		TypedQuery<Rol> myQuery = this.entityManager.createQuery("SELECT r FROM Rol r", Rol.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarRol(Rol Rol) {
		this.entityManager.merge(Rol);
	}
	
	@Override
	public Rol buscarRol(Integer id) {
		return this.entityManager.find(Rol.class, id);
	}

	@Override
	public void eliminarRol(Integer id) {
		Rol p = this.buscarRol(id);
		this.entityManager.remove(p);
	}

	@Override
	public Rol buscarRolNombre(String nombre) {
		TypedQuery<Rol> myQuery = this.entityManager.createQuery("SELECT r FROM Rol r WHERE r.nombre=:nombre", Rol.class);
		myQuery.setParameter("nombre", nombre);
		return myQuery.getSingleResult();
	}
}
