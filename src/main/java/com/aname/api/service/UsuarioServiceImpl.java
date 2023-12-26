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

	@Autowired
	private IUsuarioRepo usuarioRepo;

	@Autowired
	private IRolService rolService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public Usuario guardar(UsuarioRegistroDTO registroDTO) {

		Rol perfil = this.rolService.buscarRolCodigo(registroDTO.getRol());

		Usuario usuario = new Usuario(registroDTO.getApellidos(), registroDTO.getNombres(), registroDTO.getEmail(),
				passwordEncoder.encode(registroDTO.getPassword()), registroDTO.getEstado(), registroDTO.getDireccion(),
				registroDTO.getCiudad(), registroDTO.getSexo(), registroDTO.getFechaNacimiento(),
				Arrays.asList(perfil));

		usuario.setEstado(registroDTO.getEstado());

		this.usuarioRepo.insertarUsuario(usuario);

		Usuario u = this.usuarioRepo.buscarUsuarioPorNombreUsuario(registroDTO.getEmail());

		return u;
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
		Collection<Rol> roles = new ArrayList<Rol>();
		roles.add(perfil);
		usuario.setRoles(roles);
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

			if (!usuario.getRoles().isEmpty()) {
				usuarioDTO.setRol(usuario.getRoles().stream().findFirst().get().getCodigo());
			}
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
		return new User(usuario.getEmail(), usuario.getPassword(), mapearAutoridadesRoles(usuario.getRoles()));
	}
	
	
	private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getCodigo())).collect(Collectors.toList());
	}

}
