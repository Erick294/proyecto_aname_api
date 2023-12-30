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

	@Override
	public void insertarDocumento(DocumentoUsuarios Documento) {
		this.entityManager.persist(Documento);
		
	}

	@Override
	public DocumentoUsuarios buscarDocumento(Integer id) {
		return this.entityManager.find(DocumentoUsuarios.class, id);
	}

	@Override
	public List<DocumentoUsuarios> buscarTodosDocumento() {
		TypedQuery<DocumentoUsuarios> myQuery = this.entityManager.createQuery("SELECT r FROM DocumentoUsuarios r", DocumentoUsuarios.class);
		return myQuery.getResultList();

	}

	@Override
	public void actualizarDocumento(DocumentoUsuarios Documento) {
		this.entityManager.merge(Documento);
		
	}

	@Override
	public void eliminarDocumento(Integer id) {
		DocumentoUsuarios p = this.buscarDocumento(id);
		this.entityManager.remove(p);
		
	}

}
