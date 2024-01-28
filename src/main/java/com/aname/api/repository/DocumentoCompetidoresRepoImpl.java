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

	@Override
	public void insertarDocumento(DocumentoCompetidores Documento) {
		this.entityManager.persist(Documento);
		
	}

	@Override
	public DocumentoCompetidores buscarDocumento(Integer id) {
		return this.entityManager.find(DocumentoCompetidores.class, id);
	}

	@Override
	public List<DocumentoCompetidores> buscarTodosDocumento() {
		TypedQuery<DocumentoCompetidores> myQuery = this.entityManager.createQuery("SELECT r FROM DocumentoCompetidores r", DocumentoCompetidores.class);
		return myQuery.getResultList();

	}

	@Override
	public void actualizarDocumento(DocumentoCompetidores Documento) {
		this.entityManager.merge(Documento);
		
	}

	@Override
	public void eliminarDocumento(Integer id) {
		DocumentoCompetidores p = this.buscarDocumento(id);
		this.entityManager.remove(p);
		
	}

}
