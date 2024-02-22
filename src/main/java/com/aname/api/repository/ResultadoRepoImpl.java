package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.Resultado;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional

public class ResultadoRepoImpl implements IResultadoRepo{

    @PersistenceContext
	private EntityManager entityManager;

	/**
	* Método que permite insertar un Resultado en la base de datos
	* @param resultado - Objeto Resultado a insertar
	*/
	@Override
	public void insertarResultado(Resultado resultado) {
		this.entityManager.persist(resultado);
	}

	/**
	* Busca un resultado en la base de datos por su id
	* @param id - Id del resultado a buscar
	* @return Retorna el objeto de tipo Resultado
	*/
	@Override
	public Resultado buscarResultado(Integer id) {
		return this.entityManager.find(Resultado.class, id);
	}

	/**
	* Busca todos los resultados en la base de datos
	* @return Lista de todos los resultados de la base de datos
	*/
	@Override
	public List<Resultado> buscarTodosResultado() {
		
		TypedQuery<Resultado> myQuery = this.entityManager.createQuery("SELECT c FROM Resultado c", Resultado.class);
		return myQuery.getResultList();
	}

	/**
	* Actualiza un Resultado en la base de datos
	* @param resultado - Objeto Resultado a actualizar
	*/
	@Override
	public void actualizarResultado(Resultado resultado) {
		this.entityManager.merge(resultado);
	}

	/**
	* Eliminar un resultado en la base de datos
	* @param id - Id del resultado a eliminar
	*/
	@Override
	public void eliminarResultado(Integer id) {
		Resultado c = this.entityManager.getReference(Resultado.class, id);
		this.entityManager.remove(c);
	}
    
}
