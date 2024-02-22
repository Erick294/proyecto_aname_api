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

	/**
	* Método que inserta una Categoría en la base de datos
	* @param categoria - Categoría a insertar en la base de datos
	*/
	@Override
	public void insertarCategoria(Categoria categoria) {
		this.entityManager.persist(categoria);
	}

	/**
	* Busca una categoría en la base de datos
	* @param id - Identificador de la categoría a buscar
	* @return El objeto Categoria de ser encontrada
	*/
	@Override
	public Categoria buscarCategoria(Integer id) {
		return this.entityManager.find(Categoria.class, id);
	}

	/**
	* Busca todas las categorías del sistema
	* @return Lista de categorías que contiene la base de datos
	*/
	@Override
	public List<Categoria> buscarTodosCategoria() {

		TypedQuery<Categoria> myQuery = this.entityManager.createQuery("SELECT c FROM Categoria c", Categoria.class);
		return myQuery.getResultList();
	}

	/**
	* Actualiza una categoría en la base de datos
	* @param categoria - Categoría a actualizar en la base de datos
	*/
	@Override
	public void actualizarCategoria(Categoria categoria) {
		this.entityManager.merge(categoria);
	}

	/**
	* Elimina una categoría del sistema
	* @param id - Identificador de la categoria a eliminar
	*/
	@Override
	public void eliminarCategoria(Integer id) {
		Categoria c = this.entityManager.getReference(Categoria.class, id);
		this.entityManager.remove(c);
	}

	/**
	* Obtiene una categoría por edad y género
	* @param edad - Parametro edad por la que buscar
	* @param genero - Parametro genero por la que buscar
	* @return Categorías que coinciden con la edad y el género o nulo si no existen
	*/
	@Override
	public Categoria obtenerCategoriaPorEdadYGenero(Integer edad, String genero) {
	    TypedQuery<Categoria> myQuery = this.entityManager.createQuery(
	        "SELECT c FROM Categoria c WHERE :edad BETWEEN c.edadMinima AND c.edadMaxima AND c.genero = :genero", Categoria.class);

	    myQuery.setParameter("edad", edad);
	    myQuery.setParameter("genero", genero);

	    
	    return myQuery.getSingleResult();
	}
}
