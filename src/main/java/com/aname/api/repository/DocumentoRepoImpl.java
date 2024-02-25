package com.aname.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aname.api.model.DocumentoUsuarios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class DocumentoRepoImpl implements IDocumentosRepo {
	

	@PersistenceContext
	private EntityManager entityManager;

	/**
	* Método de inserción de un documento en la base de datos.
	* @param documento - Objeto a insertar
	*/
	@Override
	public void insertarDocumento(DocumentoUsuarios documento) {
		this.entityManager.persist(documento);
		
	}

	/**
	* Busca documento de usuario por id
	* @param id - Identificador del documento que se desea buscar
	* @return Objeto de tipo DocumentoUsuarios correspondientes al id especificado
	*/
	@Override
	public DocumentoUsuarios buscarDocumento(Integer id) {
		return this.entityManager.find(DocumentoUsuarios.class, id);
	}

	/**
	* Busca todos los documentos de los usuarios de la base de datos
	* @return Lista de documentos de usuarios
	*/
	@Override
	public List<DocumentoUsuarios> buscarTodosDocumento() {
		TypedQuery<DocumentoUsuarios> myQuery = this.entityManager.createQuery("SELECT r FROM DocumentoUsuarios r", DocumentoUsuarios.class);
		return myQuery.getResultList();

	}

	/**
	* Actualiza un documento en la base de datos
	* @param documento - El objeto documento a actualizar
	*/
	@Override
	public void actualizarDocumento(DocumentoUsuarios documento) {
		this.entityManager.merge(documento);
		
	}

	/**
	* Eliminar un documento de la base de datos
	* @param id - Id del documento a eliminar
	*/
	@Override
	public void eliminarDocumento(Integer id) {
		DocumentoUsuarios p = this.buscarDocumento(id);
		this.entityManager.remove(p);
		
	}

}
