package com.aname.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.Rol;
import com.aname.api.repository.IRolRepo;

@Service
public class RolServiceImpl implements IRolService{
	@Autowired
	private IRolRepo RolRepo;

	@Override
	public void insertarRol(Rol Rol) {
		this.RolRepo.insertarRol(Rol);
	}

	@Override
	public Rol buscarRol(Integer id) {
		return this.RolRepo.buscarRol(id);
	}

	@Override
	public List<String> buscarTodosRol() {
	    List<Rol> roles = this.RolRepo.buscarTodosRol();
	    List<String> nombresRoles = new ArrayList<String>();
	    for(Rol rol : roles) {
	        nombresRoles.add(rol.getNombre());
	    }
	    return nombresRoles;
	}

	@Override
	public void actualizarRol(Rol Rol) {
		this.RolRepo.actualizarRol(Rol);
	}

	@Override
	public void eliminarRol(Integer id) {
		this.RolRepo.eliminarRol(id);
	}
	
	@Override
	public Rol buscarRolNombre(String nombre) {
		return this.RolRepo.buscarRolNombre(nombre);
	}

}
