package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.DocumentoCompetidores;


public interface IDocumentosCompetidoresRepo {
	
	void insertarDocumento(DocumentoCompetidores Documentos);

	DocumentoCompetidores buscarDocumento(Integer id);

	List<DocumentoCompetidores> buscarTodosDocumento();

	void actualizarDocumento(DocumentoCompetidores documento);

	void eliminarDocumento(Integer id);
	

}
