package com.aname.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.Rol;
import com.aname.api.repository.IRolRepo;

@Service
public class RolServiceImpl implements IRolService {
	
	@Autowired
	private IRolRepo rolRepo;

	/**
	 * Inserta un nuevo rol en el sistema.
	 *
	 * @param rol El objeto Rol a ser insertado.
	 */
	@Override
	public void insertarRol(Rol rol) {
		this.rolRepo.insertarRol(rol);
	}

	/**
	 * Busca y devuelve un rol según su identificador.
	 *
	 * @param id El identificador del rol.
	 * @return El objeto Rol correspondiente al identificador proporcionado.
	 */
	@Override
	public Rol buscarRol(Integer id) {
		return this.rolRepo.buscarRol(id);
	}

	/**
	 * Busca y devuelve una lista de nombres de todos los roles en el sistema.
	 *
	 * @return Una lista de nombres de roles.
	 */
	@Override
	public List<String> buscarTodosRol() {
		List<Rol> roles = this.rolRepo.buscarTodosRol();
	    List<String> nombresRoles = new ArrayList<String>();
	    for(Rol rol : roles) {
	        nombresRoles.add(rol.getCodigo());
	    }
	    return nombresRoles;
	}

	/**
	 * Actualiza la información de un rol en el sistema.
	 *
	 * @param rol El objeto Rol con la información actualizada.
	 */
	@Override
	public void actualizarRol(Rol rol) {
		this.rolRepo.actualizarRol(rol);
	}

	/**
	 * Elimina un rol del sistema según su identificador.
	 *
	 * @param id El identificador del rol a ser eliminado.
	 */
	@Override
	public void eliminarRol(Integer id) {
		this.rolRepo.eliminarRol(id);
	}

	/**
	 * Busca y devuelve un rol según su código.
	 *
	 * @param codigo El código del rol a buscar.
	 * @return El objeto Rol correspondiente al código proporcionado.
	 */
	@Override
	public Rol buscarRolCodigo(String codigo) {
		return this.rolRepo.buscarRolCodigo(codigo);
	}
}
