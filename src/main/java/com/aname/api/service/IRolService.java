package com.aname.api.service;

import java.util.List;

import com.aname.api.model.Rol;

public interface IRolService {
	
	void insertarRol(Rol rol);

	Rol buscarRol(Integer id);

	List<String> buscarTodosRol();

	void actualizarRol(Rol rol);

	void eliminarRol(Integer id);
	
	Rol buscarRolCodigo(String codigo);

}
