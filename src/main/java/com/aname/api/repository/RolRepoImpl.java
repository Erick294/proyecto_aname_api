package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Rol;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class RolRepoImpl implements IRolRepo {
	

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarRol(Rol rol) {
		this.entityManager.persist(rol);
		
	}

	@Override
	public Rol buscarRol(Integer id) {
		return this.entityManager.find(Rol.class, id);
	}

	@Override
	public List<Rol> buscarTodosRol() {
		TypedQuery<Rol> myQuery = this.entityManager.createQuery("SELECT r FROM Rol r", Rol.class);
		return myQuery.getResultList();

	}

	@Override
	public void actualizarRol(Rol rol) {
		this.entityManager.merge(rol);
		
	}

	@Override
	public void eliminarRol(Integer id) {
		Rol p = this.buscarRol(id);
		this.entityManager.remove(p);
		
	}

	@Override
	public Rol buscarRolCodigo(String codigo) {
		TypedQuery<Rol> myQuery = this.entityManager.createQuery("SELECT r FROM Rol r WHERE r.codigo=:codigo", Rol.class);
		myQuery.setParameter("codigo", codigo);
		return myQuery.getSingleResult();
	}

}
