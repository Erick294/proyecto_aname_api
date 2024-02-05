package com.aname.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aname.api.model.AsociacionDeportiva;
import com.aname.api.model.DocumentoCompetidores;
import com.aname.api.model.DocumentoUsuarios;
import com.aname.api.model.Rol;
import com.aname.api.model.Usuario;
import com.aname.api.repository.IUsuarioRepo;
import com.aname.api.service.to.AsociacionCostoDTO;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.UsuarioRegistroDTO;
import com.aname.api.service.to.UsuarioResDTO;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private IUsuarioRepo usuarioRepo;

	@Autowired
	private IRolService rolService;

	@Autowired
	private IAsociacionDeportivaService asociacionDeportivaService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	private String tokenSAS = "?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-29T22:19:13Z&st=2024-01-29T14:19:13Z&spr=https,http&sig=5n44N%2BrVDmWYMuwzu0fJDpNDg9knKZErKMN6uetY2gE%3D";


	@Override
	@Transactional
	public UsuarioRegistroDTO guardar(UsuarioRegistroDTO registroDTO) {

		Rol perfil = this.rolService.buscarRolCodigo(registroDTO.getRol());
		AsociacionDeportiva a = this.asociacionDeportivaService
				.buscarAsociacionDeportiva(registroDTO.getIdAsociacion());

		List<AsociacionDeportiva> asos = new ArrayList<AsociacionDeportiva>();
		asos.add(a);
		Usuario usuario = new Usuario(registroDTO.getApellidos(), registroDTO.getNombres(), registroDTO.getEmail(),
				passwordEncoder.encode(registroDTO.getPassword()), false, registroDTO.getDireccion(),
				registroDTO.getCiudad(), registroDTO.getSexo(), registroDTO.getFechaNacimiento(), perfil);

		usuario.setEstado(false);
		usuario.setSocio(false);
		usuario.setAsociaciones(asos);
		List<Usuario> usuarios = a.getUsuarios();
		usuarios.add(usuario);
		a.setUsuarios(usuarios);
		DocumentoUsuarios docI = new DocumentoUsuarios();

		DocumentoUsuarios docF = new DocumentoUsuarios();
		DocResponseDTO docFR = registroDTO.getFotografia();
		List<DocumentoUsuarios> documentos = new ArrayList<DocumentoUsuarios>();

		if (docFR != null) {
			docF.setExtension(docFR.getExtension());
			docF.setLink(docFR.getLink());
			docF.setNombre(docFR.getNombre());
			docF.setUsuario(usuario);
			documentos.add(docF);
		}

		DocResponseDTO docIR = registroDTO.getDocumentoIdentidad();
		if (docIR != null) {
			docI.setExtension(docIR.getExtension());
			docI.setLink(docIR.getLink());
			docI.setNombre(docIR.getNombre());
			docI.setUsuario(usuario);
			documentos.add(docI);
		}
		// doc.setUsuario(usuario);

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
	

		return usuarioDTO;
	}
	@Override
	public Integer buscarIDAsociacionUsuario(String email) {
		return this.usuarioRepo.buscarUsuarioPorNombreUsuario(email).getAsociaciones().get(0).getId();
		
	}
	@Override
	public AsociacionCostoDTO buscarCostoAsociacion(Integer idAsociacion) {
		AsociacionCostoDTO ac = new AsociacionCostoDTO();
		AsociacionDeportiva a= this.asociacionDeportivaService.buscarAsociacionDeportiva(idAsociacion);
		
		ac.setCosto(a.getPrecioAsociacion().getCosto());
		ac.setId(idAsociacion);
		ac.setInstitucionFinanciera(a.getPrecioAsociacion().getInstitucionFinanciera());
		ac.setNombre(a.getNombre());
		ac.setTitular(a.getPrecioAsociacion().getTitularCuenta());
		ac.setNumeroCuenta(a.getPrecioAsociacion().getCuentaBancaria());
		
		return ac;
	}

	@Override
	public void aprobrarRegistroUsuario(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);
		usuario.setEstado(true);
		this.actualizarUsuario(usuario);
	}

	@Override
	public void negarRegistroUsuario(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);

		AsociacionDeportiva as = this.asociacionDeportivaService
				.buscarAsociacionDeportiva(usuario.getAsociaciones().get(0).getId());
		
		List<Usuario> usuariosAs= as.getUsuarios();
		usuariosAs.remove(usuario);
		
		as.setUsuarios(usuariosAs);

		this.asociacionDeportivaService.actualizarAsociacion(as);
		
		this.usuarioRepo.eliminarUsuario(usuario.getId());
		
	}

	@Override
	public void registrarPagoAsociacion(DocResponseDTO u) {
		Usuario usuario = this.buscarUsuarioPorEmail(u.getUsername());

		DocumentoUsuarios doc = new DocumentoUsuarios();

		List<DocumentoUsuarios> documentoUsuarios = usuario.getDocumentos();
		doc.setExtension(u.getExtension());
		doc.setLink(u.getLink());
		doc.setNombre(u.getNombre());
		doc.setUsuario(usuario);

		documentoUsuarios.add(doc);

		usuario.setDocumentos(documentoUsuarios);
		this.actualizarUsuario(usuario);

	}

	@Override
	public void aprobarUsuarioAsociado(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);
		usuario.setSocio(true);
		this.actualizarUsuario(usuario);
	}

	@Override
	public void negarUsuarioAsociado(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);
		usuario.setSocio(false);
		this.actualizarUsuario(usuario);
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
	public List<UsuarioResDTO> listarUsuariosRegistradosPorAsociacion(Integer idAsociacion) {
		List<Usuario> usuarios = this.usuarioRepo.buscarUsuariosRegistradosAsociacion(idAsociacion);
		List<UsuarioResDTO> usuariosDTO = new ArrayList<UsuarioResDTO>();
		for (Usuario usuario : usuarios) {
			UsuarioResDTO usuarioDTO = new UsuarioResDTO();
			usuarioDTO.setApellidos(usuario.getApellidos());
			usuarioDTO.setNombres(usuario.getNombres());
			usuarioDTO.setEmail(usuario.getEmail());
			usuarioDTO.setCiudad(usuario.getCiudad());
			usuarioDTO.setDireccion(usuario.getDireccion());
			usuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
			usuarioDTO.setSexo(usuario.getSexo());
			usuarioDTO.setEstado(usuario.getEstado());
			usuarioDTO.setRol(usuario.getRol().getCodigo());
			usuarioDTO.setIdAsociacion(usuario.getAsociaciones().get(0).getId());
			usuarioDTO.setSocio(usuario.getSocio());

			List<DocumentoUsuarios> documentosC = usuario.getDocumentos();

			boolean tienePagoAsociacion = documentosC.stream()
					.anyMatch(docComp -> docComp.getNombre().startsWith("pago-asociacion"));
			
			if(tienePagoAsociacion) {
				Optional<DocumentoUsuarios> documentoComprobantePagoOptional = documentosC.stream()
						.filter(docComp -> docComp.getNombre().startsWith("pago-asociacion")).findFirst();

				DocumentoUsuarios documentoComprobantePago = documentoComprobantePagoOptional.get();

				usuarioDTO.setPagoAsociacion(documentoComprobantePago.getLink()+tokenSAS);
			}
			

			boolean tieneDocIdentidad = documentosC.stream()
					.anyMatch(docComp -> docComp.getNombre().startsWith("doc-identidad"));
			
			if(tieneDocIdentidad) {
				Optional<DocumentoUsuarios> documentoIdentidadOptional = documentosC.stream()
						.filter(docComp -> docComp.getNombre().startsWith("doc-identidad")).findFirst();

				DocumentoUsuarios documentoIdentidad = documentoIdentidadOptional.get();

				usuarioDTO.setDocumentoIdentidad(documentoIdentidad.getLink()+tokenSAS);
			}

			
			usuariosDTO.add(usuarioDTO);
		}

		return usuariosDTO;
	}

	@Override
	public void actualizarUsuarioDTO(UsuarioRegistroDTO registroDTO) {
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

			// if (!usuario.getRoles().isEmpty()) {
			// usuarioDTO.setRol(usuario.getRoles().stream().findFirst().get().getCodigo());
			// }
			usuariosDTO.add(usuarioDTO);
		}

		return usuariosDTO;
	}
	

	@Override
	public boolean existeNombreUsuario(String email) {
		Usuario usuario = this.usuarioRepo.buscarUsuarioPorNombreUsuario(email);
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
