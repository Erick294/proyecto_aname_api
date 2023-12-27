package com.aname.api.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.aname.api.model.Usuario;
import com.aname.api.service.to.UsuarioRegistroDTO;

public interface IUsuarioService extends UserDetailsService{
	
	UsuarioRegistroDTO guardar(UsuarioRegistroDTO registroDTO);
	
	void insertarUsuario(Usuario usuario);
	
	Usuario buscarUsuario(Integer id);
	
	List<Usuario> listarUsuarios();
	
	Usuario buscarUsuarioPorEmail(String email);
	
	void actualizarUsuario(UsuarioRegistroDTO registroDTO);
	
	void actualizarUsuario(Usuario usuario);
	
	public List<UsuarioRegistroDTO> listarUsuariosTO();
	
	boolean existeNombreUsuario(String email);
	
	

}
