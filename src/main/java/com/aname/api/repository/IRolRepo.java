package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Rol;

public interface IRolRepo {
	
	void insertarRol(Rol rol);

	Rol buscarRol(Integer id);

	List<Rol> buscarTodosRol();

	void actualizarRol(Rol perfil);

	void eliminarRol(Integer id);
	
	Rol buscarRolCodigo(String codigo);

}
