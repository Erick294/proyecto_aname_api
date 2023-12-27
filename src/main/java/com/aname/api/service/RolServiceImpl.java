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


	@Override
	public void insertarRol(Rol rol) {
		this.rolRepo.insertarRol(rol);
	}

	@Override
	public Rol buscarRol(Integer id) {
		return this.rolRepo.buscarRol(id);
	}

	@Override
	public List<String> buscarTodosRol() {
		List<Rol> roles = this.rolRepo.buscarTodosRol();
	    List<String> nombresRoles = new ArrayList<String>();
	    for(Rol rol : roles) {
	        nombresRoles.add(rol.getCodigo());
	    }
	    return nombresRoles;
	}

	@Override
	public void actualizarRol(Rol rol) {
		this.rolRepo.actualizarRol(rol);
	}

	@Override
	public void eliminarRol(Integer id) {
		this.rolRepo.eliminarRol(id);
	}

	@Override
	public Rol buscarRolCodigo(String codigo) {
		return this.rolRepo.buscarRolCodigo(codigo);
	}
	
	

}
