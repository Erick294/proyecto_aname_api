package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Rol;

public interface IRolRepo {
	
	void insertarRol(Rol rol);

	List<Rol> buscarTodosRol();

	void actualizarRol(Rol rol);

	void eliminarRol(Integer id);
	
	Rol buscarRolNombre(String nombre);

	Rol buscarRol(Integer id);
	
}
