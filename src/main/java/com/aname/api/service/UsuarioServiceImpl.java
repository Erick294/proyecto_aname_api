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


	/**
	 * Registra un nuevo usuario en el sistema.
	 *
	 * @param registroDTO Los datos de registro del usuario.
	 * @return Un objeto UsuarioRegistroDTO con la información del usuario registrado.
	 */
	@Override
	@Transactional
	public UsuarioRegistroDTO guardar(UsuarioRegistroDTO registroDTO) {

		Rol perfil = this.rolService.buscarRolCodigo(registroDTO.getRol());
		AsociacionDeportiva a = this.asociacionDeportivaService
				.buscarAsociacionDeportiva(registroDTO.getIdAsociacion());

		// Configurar las asociaciones del usuario
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

		// Configurar los documentos del usuario (fotografía y documento de identidad)
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

		usuario.setDocumentos(documentos);
		this.usuarioRepo.insertarUsuario(usuario);

		// Crear un nuevo objeto UsuarioRegistroDTO con la información del usuario registrado
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

	/**
	 * Busca y devuelve el ID de la asociación deportiva a la que pertenece un usuario.
	 *
	 * @param email El correo electrónico del usuario.
	 * @return El ID de la asociación deportiva a la que pertenece el usuario.
	 */
	@Override
	public Integer buscarIDAsociacionUsuario(String email) {
		return this.usuarioRepo.buscarUsuarioPorNombreUsuario(email).getAsociaciones().get(0).getId();
		
	}

	/**
	 * Busca y devuelve la información del costo asociado a una asociación deportiva.
	 *
	 * @param idAsociacion El ID de la asociación deportiva.
	 * @return Un objeto AsociacionCostoDTO con la información del costo asociado.
	 */
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

	/**
	 * Aprueba el registro de un usuario cambiando su estado a activo.
	 *
	 * @param email El correo electrónico del usuario.
	 */
	@Override
	public void aprobrarRegistroUsuario(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);
		usuario.setEstado(true);
		this.actualizarUsuario(usuario);
	}

	/**
	 * Niega el registro de un usuario eliminándolo de la asociación deportiva.
	 *
	 * @param email El correo electrónico del usuario.
	 */
	@Override
	@Transactional
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

	/**
	 * Registra el pago de asociación de un usuario.
	 *
	 * @param u El objeto DocResponseDTO que contiene la información del pago.
	 */
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

	/**
	 * Aprueba a un usuario asociado cambiando su estado a socio.
	 *
	 * @param email El correo electrónico del usuario.
	 */
	@Override
	public void aprobarUsuarioAsociado(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);
		usuario.setSocio(true);
		this.actualizarUsuario(usuario);
	}

	/**
	 * Niega la condición de socio a un usuario asociado cambiando su estado a no socio.
	 *
	 * @param email El correo electrónico del usuario.
	 */
	@Override
	public void negarUsuarioAsociado(String email) {
		Usuario usuario = this.buscarUsuarioPorEmail(email);
		usuario.setSocio(false);
		this.actualizarUsuario(usuario);
	}

	/**
	 * Inserta un nuevo usuario en el sistema.
	 *
	 * @param usuario El objeto Usuario a ser insertado.
	 */
	@Override
	public void insertarUsuario(Usuario usuario) {
		this.usuarioRepo.insertarUsuario(usuario);
	}

	/**
	 * Busca y devuelve un usuario según su identificador.
	 *
	 * @param id El identificador del usuario.
	 * @return El objeto Usuario correspondiente al identificador proporcionado.
	 */
	@Override
	public Usuario buscarUsuario(Integer id) {
		if (id == null) {
			return null;
		}
		return this.usuarioRepo.buscarUsuario(id);
	}

	/**
	 * Lista todos los usuarios del sistema.
	 *
	 * @return Una lista de objetos Usuario.
	 */
	@Override
	public List<Usuario> listarUsuarios() {
		return this.usuarioRepo.buscarTodosUsuario();
	}

	/**
	 * Busca y devuelve un usuario según su correo electrónico.
	 *
	 * @param email El correo electrónico del usuario.
	 * @return El objeto Usuario correspondiente al correo electrónico proporcionado.
	 */
	@Override
	public Usuario buscarUsuarioPorEmail(String email) {
		return this.usuarioRepo.buscarUsuarioPorNombreUsuario(email);
	}

	/**
	 * Lista todos los usuarios registrados por una asociación deportiva.
	 *
	 * @param idAsociacion El ID de la asociación deportiva.
	 * @return Una lista de objetos UsuarioResDTO con la información de los usuarios registrados.
	 */
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

			// Verificar si el usuario tiene un pago de asociación
			boolean tienePagoAsociacion = documentosC.stream()
					.anyMatch(docComp -> docComp.getNombre().startsWith("pago-asociacion"));
			
			if(tienePagoAsociacion) {
				Optional<DocumentoUsuarios> documentoComprobantePagoOptional = documentosC.stream()
						.filter(docComp -> docComp.getNombre().startsWith("pago-asociacion")).findFirst();

				DocumentoUsuarios documentoComprobantePago = documentoComprobantePagoOptional.get();

				usuarioDTO.setPagoAsociacion(documentoComprobantePago.getLink()+tokenSAS);
			}
			
			// Verificar si el usuario tiene un documento de identidad
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

	/**
	 * Actualiza la información de un usuario según sus datos de registro.
	 *
	 * @param registroDTO Los datos actualizados del usuario.
	 */
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

	/**
	 * Actualiza la información de un usuario en el sistema.
	 *
	 * @param usuario El objeto Usuario con la información actualizada.
	 */
	@Override
	public void actualizarUsuario(Usuario usuario) {
		this.usuarioRepo.actualizarUsuario(usuario);
	}

	/**
	 * Lista todos los usuarios del sistema en formato de transferencia de objetos (DTO).
	 *
	 * @return Una lista de objetos UsuarioRegistroDTO con la información de los usuarios.
	 */
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
	
	/**
	 * Verifica si un nombre de usuario (correo electrónico) ya existe en el sistema.
	 *
	 * @param email El correo electrónico a verificar.
	 * @return true si el correo electrónico ya existe, false en caso contrario.
	 */
	@Override
	public boolean existeNombreUsuario(String email) {
		Usuario usuario = this.usuarioRepo.buscarUsuarioPorNombreUsuario(email);
		return usuario != null;
	}

	/**
	 * Implementación del método de la interfaz UserDetailsService.
	 * Carga un usuario por su nombre de usuario (correo electrónico).
	 *
	 * @param username El nombre de usuario (correo electrónico) del usuario a cargar.
	 * @return Un objeto UserDetails que representa al usuario cargado.
	 * @throws UsernameNotFoundException Si el usuario no se encuentra en el sistema.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = this.usuarioRepo.buscarUsuarioPorNombreUsuario(username);
		if (usuario == null) {
			System.out.println(username);
			throw new UsernameNotFoundException("Usuario o password inválidos");

		}
		return new User(usuario.getEmail(), usuario.getPassword(), mapearAutoridadesRoles(usuario.getRol()));
	}

	/**
	 * Mapea las autoridades (roles) del usuario a objetos GrantedAuthority.
	 *
	 * @param rol El objeto Rol del usuario.
	 * @return Una colección de GrantedAuthority representando las autoridades del usuario.
	 */
	private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Rol rol) {
		// Crear una SimpleGrantedAuthority con el código del rol
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol.getCodigo());

		// Devolver una lista con la única autoridad
		return Collections.singletonList(authority);
	}

}
