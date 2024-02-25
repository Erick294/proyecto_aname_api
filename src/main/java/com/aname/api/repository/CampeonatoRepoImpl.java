package com.aname.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Campeonato;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class CampeonatoRepoImpl implements ICampeonatoRepo {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	* El método introduce un Campeonato en la base de datos.
	* @param campeonato - Objeto que contiene la información del campeonato a insertar
	*/
	@Override
	public void insertarCampeonato(Campeonato campeonato) {
		System.out.println("JPA REPO");
		this.entityManager.persist(campeonato);
	}

	/**
	* Busca un campeonato en la base de datos
	* @param id - El identificador del campeonato a buscar
	* @return Objeto de la base de datos especificada por parámetro
	*/
	@Override
	public Campeonato buscarCampeonato(Integer id) {
		return this.entityManager.find(Campeonato.class, id);
	}

	/**
	* Busca todas las campeonatos en la base de datos.
	* @return Lista de Campeonatos que contiene la base de datos
	*/
	@Override
	public List<Campeonato> buscarTodosCampeonato() {

		TypedQuery<Campeonato> myQuery = this.entityManager.createQuery("SELECT c FROM Campeonato c", Campeonato.class);
		return myQuery.getResultList();
	}

	/**
	* Actualizar un campeonato en la base de datos.
	* @param campeonato - Campeonato a actualizar en la base de datos
	*/
	@Override
	public void actualizarCampeonato(Campeonato campeonato) {
		this.entityManager.merge(campeonato);
	}

	/**
	* Eliminación de un campeonato en la base de datos.
	* @param id - Identificador del campeonato a ser eliminado
	*/
	@Override
	public void eliminarCampeonato(Integer id) {
		Campeonato c = this.entityManager.getReference(Campeonato.class, id);
		this.entityManager.remove(c);
	}

	/**
	* Busca todos los campeonatos disponibles
	* @return lista de campeonatos disponibles
	*/
	@Override
	public List<Campeonato> buscarCampeonatosDisponibles() {

		TypedQuery<Campeonato> myQuery = this.entityManager.createQuery(
			    "SELECT c FROM Campeonato c WHERE :fechaActual >= c.inscripcionInicio AND :fechaActual <= c.inscripcionFin", 
			    Campeonato.class);
		myQuery.setParameter("fechaActual", LocalDateTime.now());

		System.out.println("Cuenta: " + myQuery.getResultList().size());
		System.out.print("Fecha actual: " + LocalDateTime.now());

		return myQuery.getResultList();
	}

}
