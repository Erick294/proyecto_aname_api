package com.aname.api.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.aname.api.model.Usuario;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.UsuarioRegistroDTO;
import com.aname.api.service.to.UsuarioResDTO;

public interface IUsuarioService extends UserDetailsService{
	
	UsuarioRegistroDTO guardar(UsuarioRegistroDTO registroDTO);
	
	void insertarUsuario(Usuario usuario);
	
	Usuario buscarUsuario(Integer id);
	
	List<Usuario> listarUsuarios();
	
	Usuario buscarUsuarioPorEmail(String email);
	
	
	void actualizarUsuario(Usuario usuario);
	
	public List<UsuarioRegistroDTO> listarUsuariosTO();
	
	boolean existeNombreUsuario(String email);

	void aprobrarRegistroUsuario(String email);

	void aprobarUsuarioAsociado(String email);

	void negarRegistroUsuario(String email);

	void negarUsuarioAsociado(String email);

	void actualizarUsuarioDTO(UsuarioRegistroDTO registroDTO);

	List<UsuarioResDTO> listarUsuariosRegistradosPorAsociacion(Integer idAsociacion);

	void registrarPagoAsociacion(DocResponseDTO u);
	
	

}
