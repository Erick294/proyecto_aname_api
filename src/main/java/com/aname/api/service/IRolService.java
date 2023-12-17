package com.aname.api.service;

import java.util.List;

import com.aname.api.model.Rol;

public interface IRolService {
	void insertarRol(Rol Rol);

	Rol buscarRol(Integer id);

	List<String> buscarTodosRol();

	void actualizarRol(Rol Rol);

	void eliminarRol(Integer id);
	
	Rol buscarRolNombre(String nombre);
}
