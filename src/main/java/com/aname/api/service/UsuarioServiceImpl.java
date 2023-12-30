package com.aname.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aname.api.model.DocumentoUsuarios;
import com.aname.api.model.Rol;
import com.aname.api.model.Usuario;
import com.aname.api.repository.IUsuarioRepo;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.UsuarioRegistroDTO;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private IUsuarioRepo usuarioRepo;

	@Autowired
	private IRolService rolService;


	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	@Transactional
	public UsuarioRegistroDTO guardar(UsuarioRegistroDTO registroDTO) {

		Rol perfil = this.rolService.buscarRolCodigo(registroDTO.getRol());

		Usuario usuario = new Usuario(registroDTO.getApellidos(), registroDTO.getNombres(), registroDTO.getEmail(),
				passwordEncoder.encode(registroDTO.getPassword()), registroDTO.getEstado(), registroDTO.getDireccion(),
				registroDTO.getCiudad(), registroDTO.getSexo(), registroDTO.getFechaNacimiento(),
				perfil);

		usuario.setEstado(registroDTO.getEstado());
		
		DocumentoUsuarios docI = new DocumentoUsuarios();
		DocResponseDTO docIR = registroDTO.getDocumentoIdentidad();
		docI.setExtension(docIR.getExtension());
		docI.setLink(docIR.getLink());
		docI.setNombre(docIR.getNombre());
		docI.setUsuario(usuario);
		
		DocumentoUsuarios docF = new DocumentoUsuarios();
		DocResponseDTO docFR = registroDTO.getFotografia();
		docF.setExtension(docFR.getExtension());
		docF.setLink(docFR.getLink());
		docF.setNombre(docFR.getNombre());
		docF.setUsuario(usuario);
		
		//doc.setUsuario(usuario);
		List<DocumentoUsuarios> documentos = new ArrayList<DocumentoUsuarios>();
		documentos.add(docF);
		documentos.add(docI);
		
		usuario.setDocumentos(documentos);
		this.usuarioRepo.insertarUsuario(usuario);
		
		UsuarioRegistroDTO usuarioDTO = new UsuarioRegistroDTO();
		usuarioDTO.setApellidos(usuario.getApellidos());
		usuarioDTO.setNombres(usuario.getNombres());
		usuarioDTO.setEmail(usuario.getEmail());
		usuarioDTO.setCiudad(usuario.getCiudad());
		usuarioDTO.setDireccion(usuario.getDireccion());
		usuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
		usuarioDTO.setSexo(usuario.getSexo());
		usuarioDTO.setEstado(usuario.getEstado());
		usuarioDTO.setId(usuario.getId());
		usuarioDTO.setPassword(usuario.getPassword());
		usuarioDTO.setRol(usuario.getRol().getCodigo());
		usuarioDTO.setFotografia(docFR);
		usuarioDTO.setDocumentoIdentidad(docIR);
//
//		if (!usuario.getRoles().isEmpty()) {
//			usuarioDTO.setRol(usuario.getRoles().stream().findFirst().get().getCodigo());
//		}
		

		return usuarioDTO;
	}

	@Override
	public void insertarUsuario(Usuario usuario) {
		this.usuarioRepo.insertarUsuario(usuario);
	}

	@Override
	public Usuario buscarUsuario(Integer id) {
		if (id == null) {
			return null;
		}
		return this.usuarioRepo.buscarUsuario(id);
	}

	@Override
	public List<Usuario> listarUsuarios() {
		return this.usuarioRepo.buscarTodosUsuario();
	}

	@Override
	public Usuario buscarUsuarioPorEmail(String email) {
		return this.usuarioRepo.buscarUsuarioPorNombreUsuario(email);
	}

	@Override
	public void actualizarUsuario(UsuarioRegistroDTO registroDTO) {
		Usuario usuario = this.usuarioRepo.buscarUsuarioPorNombreUsuario(registroDTO.getEmail());
		if (usuario == null) {
			throw new RuntimeException("No se encontró ningún usuario con el email proporcionado.");
		}
		Rol perfil = this.rolService.buscarRolCodigo(registroDTO.getRol());

		usuario.setApellidos(registroDTO.getApellidos());
		usuario.setNombres(registroDTO.getNombres());
		usuario.setEmail(registroDTO.getEmail());
		usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
		
		usuario.setRol(perfil);
		usuario.setEstado(registroDTO.getEstado());
		usuario.setFechaNacimiento(registroDTO.getFechaNacimiento());
		usuario.setSexo(registroDTO.getSexo());
		usuario.setCiudad(registroDTO.getSexo());
		usuario.setDireccion(registroDTO.getDireccion());

		System.out.println(registroDTO.getId());

		this.usuarioRepo.actualizarUsuario(usuario);
	}

	@Override
	public void actualizarUsuario(Usuario usuario) {
		this.usuarioRepo.actualizarUsuario(usuario);
	}

	@Override
	public List<UsuarioRegistroDTO> listarUsuariosTO() {
		List<Usuario> usuarios = this.listarUsuarios();
		List<UsuarioRegistroDTO> usuariosDTO = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			UsuarioRegistroDTO usuarioDTO = new UsuarioRegistroDTO();
			usuarioDTO.setApellidos(usuario.getApellidos());
			usuarioDTO.setNombres(usuario.getNombres());
			usuarioDTO.setEmail(usuario.getEmail());
			usuarioDTO.setCiudad(usuario.getCiudad());
			usuarioDTO.setDireccion(usuario.getDireccion());
			usuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
			usuarioDTO.setSexo(usuario.getSexo());
			usuarioDTO.setEstado(usuario.getEstado());
			usuarioDTO.setRol(usuario.getRol().getCodigo());

//			if (!usuario.getRoles().isEmpty()) {
//				usuarioDTO.setRol(usuario.getRoles().stream().findFirst().get().getCodigo());
//			}
			usuariosDTO.add(usuarioDTO);
		}

		return usuariosDTO;
	}

	@Override
	public boolean existeNombreUsuario(String email) {
		Usuario usuario = this.usuarioRepo.buscarUsuarioPorNombreUsuario(email);
		System.out.println(usuario != null);
		return usuario != null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = this.usuarioRepo.buscarUsuarioPorNombreUsuario(username);
		if (usuario == null) {
			System.out.println(username);
			throw new UsernameNotFoundException("Usuario o password inválidos");

		}
		return new User(usuario.getEmail(), usuario.getPassword(), mapearAutoridadesRoles(usuario.getRol()));
	}
	
	
	private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Rol rol) {
	    // Crear una SimpleGrantedAuthority con el código del rol
	    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol.getCodigo());

	    // Devolver una lista con la única autoridad
	    return Collections.singletonList(authority);
	}


}
