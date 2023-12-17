package com.aname.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aname.api.model.Rol;
import com.aname.api.model.Usuario;
import com.aname.api.repository.IUsuarioRepo;
import com.aname.api.service.to.UsuarioRegistroDTO;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	//@Autowired
	private IUsuarioRepo usuarioRepo;

	@Autowired
	private IRolService rolService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	
	//Registrar
	@Override
	public Usuario guardar(UsuarioRegistroDTO registroDTO) {

		Rol Rol = this.rolService.buscarRolNombre(registroDTO.getRol());

		Usuario usuario = new Usuario(registroDTO.getApellidos(), registroDTO.getNombres(), registroDTO.getEmail(),
				this.passwordEncoder.encode(registroDTO.getPassword()), registroDTO.getEstado(), registroDTO.getDireccion(),registroDTO.getCiudad(), registroDTO.getSexo(), registroDTO.getFechaNacimiento(), Arrays.asList(Rol));

		return this.usuarioRepo.save(usuario);

	}

	//Actualizar
	@Override
	public void actualizarUsuario(UsuarioRegistroDTO registroDTO) {
		Usuario usuario = this.usuarioRepo.findByEmail(registroDTO.getEmail());
		if (usuario == null) {
			throw new RuntimeException("No se encontró ningún usuario con el email proporcionado.");
		}

		Rol Rol = this.rolService.buscarRolNombre(registroDTO.getRol());

		usuario.setApellidos(registroDTO.getApellidos());
		usuario.setNombres(registroDTO.getNombres());
		usuario.setEmail(registroDTO.getEmail());
		usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
		Collection<Rol> roles = new ArrayList<Rol>();
		roles.add(Rol);
		usuario.setRoles(roles);
		usuario.setEstado(registroDTO.getEstado());

		usuario.setCiudad(registroDTO.getCiudad());
		usuario.setDireccion(registroDTO.getDireccion());
		usuario.setSexo(registroDTO.getSexo());
		usuario.setFechaNacimiento(registroDTO.getFechaNacimiento());

		System.out.println(registroDTO.getId());
		this.usuarioRepo.save(usuario);
	}

	@Override
	public boolean existeNombreUsuario(String email) {
		Usuario usuario = this.usuarioRepo.findByEmail(email);
		System.out.println(usuario != null);
		return usuario != null;
	}

//	@Override
//	public void eliminar(UsuarioRegistroDTO registroDTO) {
//
//		Usuario usuario = this.usuarioRepo.findByEmail(registroDTO.getEmail());
//		this.usuarioRepo.eliminarUsuario(usuario.getId());
//	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = this.usuarioRepo.findByEmail(username);
		if (usuario == null) {
			System.out.println(username);
			throw new UsernameNotFoundException("Usuario o password inválidos");

		}
		return new User(usuario.getEmail(), usuario.getPassword(), mapearAutoridadesRoles(usuario.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
	}

	
	//Todos los usuarios
	@Override
	public List<Usuario> listarUsuarios() {
		return this.usuarioRepo.findAll();
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
			if (!usuario.getRoles().isEmpty()) {
				usuarioDTO.setRol(usuario.getRoles().stream().findFirst().get().getNombre());
			}
			usuarioDTO.setEstado(usuario.getEstado());
			usuarioDTO.setCiudad(usuario.getCiudad());
			usuarioDTO.setSexo(usuario.getSexo());
			usuarioDTO.setDireccion(usuario.getDireccion());
			usuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
			usuariosDTO.add(usuarioDTO);
		}
		return usuariosDTO;
	}

//	@Override
//	public List<UsuarioRegistroDTO> listarUsuariosPorNombreTO(String nombre) {
//		List<Usuario> usuarios = this.usuarioRepo.buscarUsuarioPorNombreUsuarioLista("%" + nombre + "%");
//		List<UsuarioRegistroDTO> usuariosDTO = new ArrayList<>();
//		for (Usuario usuario : usuarios) {
//			UsuarioRegistroDTO usuarioDTO = new UsuarioRegistroDTO();
//
//			usuarioDTO.setApellidos(usuario.getApellidos());
//			usuarioDTO.setNombres(usuario.getNombres());
//			usuarioDTO.setEmail(usuario.getEmail());
//			if (!usuario.getRoles().isEmpty()) {
//				usuarioDTO.setRol(usuario.getRoles().stream().findFirst().get().getNombre());
//			}
//			usuarioDTO.setEstado(usuario.getEstado());
//			usuarioDTO.setCiudad(usuario.getCiudad());
//			usuarioDTO.setSexo(usuario.getSexo());
//			usuarioDTO.setDireccion(usuario.getDireccion());
//			usuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
//			usuariosDTO.add(usuarioDTO);
//		}
//		return usuariosDTO;
//	}

	@Override
	public Usuario buscarPorEmail(String email) {
		return this.usuarioRepo.findByEmail(email);
	}

	@Override
	public void insertarUsuario(Usuario usuario) {
		this.usuarioRepo.save(usuario);
	}

	

//	@Override
//	public Usuario buscarUsuario(Integer id) {
//		if (id == null) {
//			return null;
//		}
//		return this.usuarioRepo.buscarUsuario(id);
//	}
//
//	
//	@Override
//	public void actualizarUsuario(Usuario usuario) {
//		this.usuarioRepo.actualizarUsuario(usuario);
//	}

//	@Override
//	public void eliminarUsuario(Integer id) {
//		this.usuarioRepo.eliminarUsuario(id);
//	}



	

}
