package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.aname.api.model.Prueba;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class PruebaRepoImpl implements IPruebaRepo {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	* Metodo para insertar una prueba en la base de datos
	* @param prueba - Objeto Prueba a insertar
	*/
	@Override
	public void insertarPrueba(Prueba prueba) {
		this.entityManager.persist(prueba);
	}

	/**
	* Busca una prueba por su id en la base de datos
	* @param id - Id del objeto de tipo Prueba
	* @return Objeto de tipo Prueba
	*/
	@Override
	public Prueba buscarPrueba(Integer id) {
		return this.entityManager.find(Prueba.class, id);
	}

	/**
	* Busca todas las pruebas de la base de datos
	* @return Lista de todas las pruebas de la base de datos
	*/
	@Override
	public List<Prueba> buscarTodosPrueba() {
		TypedQuery<Prueba> myQuery = this.entityManager.createQuery("SELECT p FROM Prueba p", Prueba.class);
		return myQuery.getResultList();
	}
	
	/**
	* Busca todas las pruebas de un campeonato por su id
	* @param idCampeonato - Id del campeonato a buscar
	* @return Lista de objetos de tipo Prueba del campeonato especificado
	*/
	@Override
	public List<Prueba> buscarPruebasPorCampeonato(Integer idCampeonato) {
	    TypedQuery<Prueba> myQuery = this.entityManager.createQuery(
	        "SELECT p FROM Prueba p JOIN p.campeonatos c WHERE c.id = :idCampeonato", Prueba.class);

	    myQuery.setParameter("idCampeonato", idCampeonato);

	    return myQuery.getResultList();
	}
	
	/**
	* Busca todas las pruebas de una categoria y un campeonato especifico
	* @param idCampeonato - Id del campeonato a buscar
	* @param idCategoria - Id de la categoría a buscar
	* @return Lista de pruebas encontradas con los parametros especificados
	*/
	@Override
	public List<Prueba> buscarPruebasPorCampeonatoYCategoria(Integer idCampeonato, Integer idCategoria) {
	    TypedQuery<Prueba> myQuery = this.entityManager.createQuery(
	        "SELECT p FROM Prueba p JOIN p.campeonatos c JOIN p.categorias cat " +
	        "WHERE c.id = :idCampeonato AND cat.id = :idCategoria", Prueba.class);

	    myQuery.setParameter("idCampeonato", idCampeonato);
	    myQuery.setParameter("idCategoria", idCategoria);

	    return myQuery.getResultList();
	}

	/**
	* Actualiza la prueba en la base de datos
	* @param prueba - El objeto Prueba a actualizar
	*/
	@Override
	public void actualizarPrueba(Prueba prueba) {
		this.entityManager.merge(prueba);
	}

	/**
	* Elimina una prueba en la base de datos
	* @param id - El id de la prueba a eliminar
	*/
	@Override
	public void eliminarPrueba(Integer id) {
		Prueba p = this.entityManager.getReference(Prueba.class, id);
		this.entityManager.remove(p);
	}

}
