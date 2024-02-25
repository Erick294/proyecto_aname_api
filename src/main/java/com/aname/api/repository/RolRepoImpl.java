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

	/**
	* Metodo que inerta un Rol en la base de datos
	* @param rol - Objeto Rol a insertar
	*/
	@Override
	public void insertarRol(Rol rol) {
		this.entityManager.persist(rol);
		
	}

	/**
	* Busca un rol en la base de datos por su id
	* @param id - Id del rol a buscar
	* @return Retorna el objeto Rol
	*/
	@Override
	public Rol buscarRol(Integer id) {
		return this.entityManager.find(Rol.class, id);
	}

	/**
	* Busca todos los roles de la base de datos
	* @return Lista de todos los roles de la base de datos
	*/
	@Override
	public List<Rol> buscarTodosRol() {
		TypedQuery<Rol> myQuery = this.entityManager.createQuery("SELECT r FROM Rol r", Rol.class);
		return myQuery.getResultList();

	}

	/**
	* Actualiza un rol en la base de datos
	* @param rol - Objeto Rol a actualizar
	*/
	@Override
	public void actualizarRol(Rol rol) {
		this.entityManager.merge(rol);
		
	}

	/**
	* Elimina un rol de la base de datos
	* @param id - El id Rol a eliminar
	*/
	@Override
	public void eliminarRol(Integer id) {
		Rol p = this.buscarRol(id);
		this.entityManager.remove(p);
		
	}

	/**
	* Busca un rol en la base de datos por su codigo
	* @param codigo - Codigo del rol a buscar
	* @return El rol con el código especificado o nulo si no existe
	*/
	@Override
	public Rol buscarRolCodigo(String codigo) {
		TypedQuery<Rol> myQuery = this.entityManager.createQuery("SELECT r FROM Rol r WHERE r.codigo=:codigo", Rol.class);
		myQuery.setParameter("codigo", codigo);
		return myQuery.getSingleResult();
	}

}
