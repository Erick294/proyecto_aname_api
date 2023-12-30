package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.DocumentoUsuarios;


public interface IDocumentosRepo {
	
	void insertarDocumento(DocumentoUsuarios Documentos);

	DocumentoUsuarios buscarDocumento(Integer id);

	List<DocumentoUsuarios> buscarTodosDocumento();

	void actualizarDocumento(DocumentoUsuarios documento);

	void eliminarDocumento(Integer id);
	

}
