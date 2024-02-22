package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class UsuarioRepoImpl implements IUsuarioRepo {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	* Insertar un usuario en la base de datos
	* @param usuario - El objeto Usuario a insertar
	*/
	@Override
	public void insertarUsuario(Usuario usuario) {
		this.entityManager.persist(usuario);

	}

	/**
	* Busca usuario en la base de datos por su id
	* @param id - Id del usuario a buscar
	* @return Retorna el Usuario con el id especificado o nulo si no existe
	*/
	@Override
	public Usuario buscarUsuario(Integer id) {
		try {

			return this.entityManager.find(Usuario.class, id);

		} catch (NoResultException e) {

			return null;
		}
	}

	/**
	* Busca todos los usuarios de la base de datos
	* @return Lista de los usuarios encontrados en la base de datos
	*/
	@Override
	public List<Usuario> buscarTodosUsuario() {
		TypedQuery<Usuario> myQuery = this.entityManager.createQuery("SELECT u FROM Usuario u", Usuario.class);
		return myQuery.getResultList();
	}

	/**
	* Actualiza un usuario en la base de datos
	* @param usuario - Usuario que se desea actualizar
	*/
	@Override
	public void actualizarUsuario(Usuario usuario) {
		this.entityManager.merge(usuario);

	}

	/**
	* Elimina un usuario en la base de datos por el id
	* @param id - Id del usuario a eliminar
	*/
	@Override
	public void eliminarUsuario(Integer id) {
		Usuario u = this.buscarUsuario(id);
		this.entityManager.remove(u);

	}

	/**
	* Busca usuario por nombre de usuario
	* @param nombreUsuario - Nombre de usuario a buscar
	* @return Regresa el objeto Usuario que pertenece al nombre de usuario
	*/
	@Override
	public Usuario buscarUsuarioPorNombreUsuario(String nombreUsuario) {
		TypedQuery<Usuario> myQuery = this.entityManager
				.createQuery("SELECT u FROM Usuario u WHERE u.email=:nombreUsuario", Usuario.class);
		myQuery.setParameter("nombreUsuario", nombreUsuario);
		try {
			return myQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	* Busca todos los usuarios registrados en una asociacion deportiva
	* @param idAsociacion - Id de la asociacion deportiva
	* @return Lista con los usuarios registrados por una asociación deportiva
	*/
	@Override
	public List<Usuario> buscarUsuariosRegistradosAsociacion(Integer idAsociacion){
		TypedQuery<Usuario> myQuery = this.entityManager
				.createQuery("SELECT u FROM Usuario u JOIN FETCH u.asociaciones as JOIN FETCH u.rol r "
						+ "WHERE as.id =:idAsociacion AND r.codigo='ATL' AND u.competidores IS EMPTY", Usuario.class);
		myQuery.setParameter("idAsociacion", idAsociacion);
		try {
			return myQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	

}
