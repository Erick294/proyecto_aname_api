package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.AsociacionDeportiva;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class AsociacionDeportivaRepoImpl implements IAsociacionDeportivaRepo{

    @PersistenceContext
	private EntityManager entityManager;

	/**
	* Inserta un Asociación Deportiva en la DB
	* @param asociacionDeportiva - La instancia de la clase Asociación Deportiva
	*/
	@Override
	public void insertarAsociacionDeportiva(AsociacionDeportiva asociacionDeportiva) {
		System.out.println("JPA REPO");
		this.entityManager.persist(asociacionDeportiva);
	}

	/**
	* Busca asociación deportiva por id.
	* @param id - identificación del registro de asociacion deportiva
	* @return objeto que contiene el registro de asociacion deportiva o nulo si no existe
	*/
	@Override
	public AsociacionDeportiva buscarAsociacionDeportiva(Integer id) {
		return this.entityManager.find(AsociacionDeportiva.class, id);
	}

	/**
	* Busca todas las asociaciones deportivas que tengan en la base de datos
	* @return Lista de todos los registros de tipo Asociación Deportiva
	*/
	@Override
	public List<AsociacionDeportiva> buscarTodosAsociacionDeportiva() {
		
		TypedQuery<AsociacionDeportiva> myQuery = this.entityManager.createQuery("SELECT c FROM AsociacionDeportiva c", AsociacionDeportiva.class);
		return myQuery.getResultList();
	}

	/**
	* Actualiza una asociación deportiva en la base de datos
	* @param asociacionDeportiva - La asociación deportiva a actualizar
	*/
	@Override
	public void actualizarAsociacionDeportiva(AsociacionDeportiva asociacionDeportiva) {
		this.entityManager.merge(asociacionDeportiva);
	}

	/**
	* Elimina una asociación deportiva en la base de datos.
	* @param id - El identificador de la asociación deportiva
	*/
	@Override
	public void eliminarAsociacionDeportiva(Integer id) {
		AsociacionDeportiva c = this.entityManager.getReference(AsociacionDeportiva.class, id);
		this.entityManager.remove(c);
	}
    
}
