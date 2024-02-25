package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.DocumentoCompetidores;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class DocumentoCompetidoresRepoImpl implements IDocumentosCompetidoresRepo {
	

	@PersistenceContext
	private EntityManager entityManager;

	/**
	* Método de inserción de un documento de competidor en la base de datos.
	* @param documento - El objeto Documento a insertar
	*/
	@Override
	public void insertarDocumento(DocumentoCompetidores documento) {
		this.entityManager.persist(documento);
		
	}

	/**
	* Busca documento de un competidores por id
	* @param id - Identificación del documento a buscar
	* @return Objeto de tipo DocumentoCompetidores que contienen la información especificada
	*/
	@Override
	public DocumentoCompetidores buscarDocumento(Integer id) {
		return this.entityManager.find(DocumentoCompetidores.class, id);
	}

	/**
	* Busca todos los documentos de competidores en la base de datos
	* @return Lista de todos los documentos de la base de datos
	*/
	@Override
	public List<DocumentoCompetidores> buscarTodosDocumento() {
		TypedQuery<DocumentoCompetidores> myQuery = this.entityManager.createQuery("SELECT r FROM DocumentoCompetidores r", DocumentoCompetidores.class);
		return myQuery.getResultList();

	}

	/**
	* Actualizar un documento de competidor en la base de datos
	* @param documento - Objeto a actualizar
	*/
	@Override
	public void actualizarDocumento(DocumentoCompetidores documento) {
		this.entityManager.merge(documento);
		
	}

	/**
	* Elimina un documento de competidor
	* @param id - Id del documento a eliminar
	*/
	@Override
	public void eliminarDocumento(Integer id) {
		DocumentoCompetidores p = this.buscarDocumento(id);
		this.entityManager.remove(p);
		
	}

}
