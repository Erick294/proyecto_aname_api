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

	/**
	* Método de inserción de un precio en la base de datos
	* @param precio - PrecioInscripción a insertar en la base de datos
	*/
	@Override
	public void insertarPrecioInscripcion(PrecioInscripcion precio) {
		this.entityManager.persist(precio);
	}

	/**
	* Busca un precio de inscripción en la base de datos
	* @param id - Id del precio a buscar
	* @return Devolución del precio de la inscripción que contenga el Id especificado
	*/
	@Override
	public PrecioInscripcion buscarPrecioInscripcion(Integer id) {
		return this.entityManager.find(PrecioInscripcion.class, id);
	}

	/**
	* Busca todos los precios de inscripciones en la base de datos
	* @return Lista de precios que contiene la información de la base de datos
	*/
	@Override
	public List<PrecioInscripcion> buscarTodosPrecioInscripcion() {
		TypedQuery<PrecioInscripcion> myQuery = this.entityManager.createQuery("SELECT p FROM PrecioInscripcion p", PrecioInscripcion.class);
		return myQuery.getResultList();
	}
	
	/**
	* Busca el precio de inscripcion por el id del campeonato
	* @param idCampeonato - Id del campeonato
	* @return PrecioInscripción encontrada para el campeonato especificado
	*/
	@Override
	public PrecioInscripcion buscarPreciosPorCampeonato(Integer idCampeonato) {
	    TypedQuery<PrecioInscripcion> myQuery = this.entityManager.createQuery(
	        "SELECT p FROM PrecioInscripcion p JOIN p.campeonato c WHERE c.id = :idCampeonato",
	        PrecioInscripcion.class
	    );

	    myQuery.setParameter("idCampeonato", idCampeonato);

	    return myQuery.getSingleResult();
	}

	/**
	* Actualizar un precio de inscripción en la base de datos
	* @param precio - Objeto PrecioInscripcion a actualizar
	*/
	@Override
	public void actualizarPrecioInscripcion(PrecioInscripcion precio) {
		this.entityManager.merge(precio);
	}

	/**
	* Elimina un precio de inscripcion de la base de datos
	* @param id - Id del precio a eliminar
	*/
	@Override
	public void eliminarPrecioInscripcion(Integer id) {
		PrecioInscripcion p = this.entityManager.getReference(PrecioInscripcion.class, id);
		this.entityManager.remove(p);
	}

}
