package com.aname.api.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.aname.api.model.Usuario;
import com.aname.api.service.to.UsuarioRegistroDTO;

public interface IUsuarioService extends UserDetailsService {

	Usuario guardar(UsuarioRegistroDTO registroDTO);

	void actualizarUsuario(UsuarioRegistroDTO registroDTO);

	boolean existeNombreUsuario(String email);

	List<Usuario> listarUsuarios();

	Usuario buscarPorEmail(String email);

	void insertarUsuario(Usuario usuario);

	List<UsuarioRegistroDTO> listarUsuariosTO();

	

}
