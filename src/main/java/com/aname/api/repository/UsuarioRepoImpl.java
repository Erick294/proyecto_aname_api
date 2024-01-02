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

	@Override
	public void insertarUsuario(Usuario usuario) {
		this.entityManager.persist(usuario);

	}

	@Override
	public Usuario buscarUsuario(Integer id) {
		try {

			return this.entityManager.find(Usuario.class, id);

		} catch (NoResultException e) {

			return null;
		}
	}

	@Override
	public List<Usuario> buscarTodosUsuario() {
		TypedQuery<Usuario> myQuery = this.entityManager.createQuery("SELECT u FROM Usuario u", Usuario.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarUsuario(Usuario usuario) {
		this.entityManager.merge(usuario);

	}

	@Override
	public void eliminarUsuario(Integer id) {
		Usuario u = this.buscarUsuario(id);
		this.entityManager.remove(u);

	}

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

}
