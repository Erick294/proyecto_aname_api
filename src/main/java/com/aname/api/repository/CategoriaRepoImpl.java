package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Categoria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class CategoriaRepoImpl implements ICategoriaRepo {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertarCategoria(Categoria categoria) {
		// System.out.println("JPA REPO");
		this.entityManager.persist(categoria);
	}

	@Override
	public Categoria buscarCategoria(Integer id) {
		return this.entityManager.find(Categoria.class, id);
	}

	@Override
	public List<Categoria> buscarTodosCategoria() {

		TypedQuery<Categoria> myQuery = this.entityManager.createQuery("SELECT c FROM Categoria c", Categoria.class);
		return myQuery.getResultList();
	}

	@Override
	public void actualizarCategoria(Categoria categoria) {
		this.entityManager.merge(categoria);
	}

	@Override
	public void eliminarCategoria(Integer id) {
		Categoria c = this.entityManager.getReference(Categoria.class, id);
		this.entityManager.remove(c);
	}

	@Override
	public Categoria obtenerCategoriaPorEdadYGenero(Integer edad, String genero) {
	    TypedQuery<Categoria> myQuery = this.entityManager.createQuery(
	        "SELECT c FROM Categoria c WHERE :edad BETWEEN c.edadMinima AND c.edadMaxima AND c.genero = :genero", Categoria.class);

	    myQuery.setParameter("edad", edad);
	    myQuery.setParameter("genero", genero);

	    
	    return myQuery.getSingleResult();
	}


}
