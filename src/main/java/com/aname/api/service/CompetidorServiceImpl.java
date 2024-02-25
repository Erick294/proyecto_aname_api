package com.aname.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.Campeonato;
import com.aname.api.model.Categoria;
import com.aname.api.model.Competidor;
import com.aname.api.model.DocumentoCompetidores;
import com.aname.api.model.Prueba;
import com.aname.api.model.Usuario;
import com.aname.api.repository.ICategoriaRepo;
import com.aname.api.repository.ICompetidorRepo;
import com.aname.api.repository.IDocumentosCompetidoresRepo;
import com.aname.api.repository.IPrecioInscripcionRepo;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.CompetidorResTO;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.DocsCompetidoresDTO;
import com.aname.api.service.to.FichaInscripcionCampTO;
import com.aname.api.service.to.PruebaResponseDTO;

@Service
public class CompetidorServiceImpl implements ICompetidorService {

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private ICampeonatoService campeonatoService;

	@Autowired
	private ICompetidorRepo competidorRepo;

	@Autowired
	private ICategoriaRepo categoriaRepo;

	@Autowired
	private IPruebaService pruebaService;

	@Autowired
	private IPrecioInscripcionRepo precioInscripcionRepo;

	@Autowired
	private IAsociacionDeportivaService asociacionDeportivaService;

	@Autowired
	private AzureBlobService azureBlobAdapter;

	@Autowired
	private IDocumentosCompetidoresRepo documentosCompetidoresRepo;

	/**
	* Metodo que regista un competidor a la base de datos con estado "Pendiente" (Registro Inicial)
	* @param c - objeto Competidor a registrar
	*/
	@Override
	public void registroInicialCompetidor(CompetidorReqTO c) {
		Competidor competidor = new Competidor();

		Usuario u = this.usuarioService.buscarUsuarioPorEmail(c.getEmail());

		competidor.setAsociacionDeportiva(u.getAsociaciones().get(0));

		//Setear los campeonatos a los que se inscribe el competidor
		List<Campeonato> campeonatos = new ArrayList<Campeonato>();
		campeonatos.add(this.campeonatoService.buscarCampeonatoPorID(c.getIdCampeonato()));

		competidor.setCampeonatos(campeonatos);
		competidor.setEstadoParticipacion("Pendiente");
		competidor.setFechaInscripcion(LocalDateTime.now());

		//Setear las pruebas a las que se inscribe el competidor
		List<Prueba> pruebas = new ArrayList<Prueba>();
		for (Integer id : c.getPruebas()) {
			pruebas.add(this.pruebaService.buscarPrueba(id));
		}
		competidor.setPruebas(pruebas);
		competidor.setUsuario(u);

		this.competidorRepo.insertarCompetidor(competidor);
	}

	/**
	* Registra un documento de pago para un competidor
	* Una vez subido el pago y la ficha de inscripción el estado del competidor cambia a "Preinscrito"
	* @param doc Documento DTO que contiene los detalles del documento a registrar
	*/
	@Override
	public void registrarPago(DocsCompetidoresDTO doc) {
		Competidor c = this.competidorRepo.buscarCompetidor(doc.getIdCompetidor());
		List<DocumentoCompetidores> documentosC = c.getDocumentos();

		// Verificar si ya existe el comprobante de pago
		boolean tieneComprobantePago = documentosC.stream()
				.anyMatch(docComp -> docComp.getNombre().startsWith("comprobante-pago"));

		// Verificar si ya existe la ficha de inscripción
		boolean tieneFichaInscripcion = documentosC.stream()
				.anyMatch(docComp -> docComp.getNombre().startsWith("ficha-inscripcion"));

		// Actualizar documentos según su existencia
		if (tieneComprobantePago) {

			// Obtener el comprobante de pago existente
			Optional<DocumentoCompetidores> documentoComprobantePagoOptional = documentosC.stream()
					.filter(docComp -> docComp.getNombre().startsWith("comprobante-pago")).findFirst();

			DocumentoCompetidores documentoComprobantePago = documentoComprobantePagoOptional.get();

			documentoComprobantePago.setExtension(doc.getExtension());
			documentoComprobantePago.setLink(doc.getLink());
			documentoComprobantePago.setNombre(doc.getNombre());

			// Actualizar comprobante de pago en el repositorio
			this.documentosCompetidoresRepo.actualizarDocumento(documentoComprobantePago);

		} else {

			// El comprobante de pago no existe, crear uno nuevo
			DocumentoCompetidores documentoComprobantePago = new DocumentoCompetidores();
			documentoComprobantePago.setExtension(doc.getExtension());
			documentoComprobantePago.setLink(doc.getLink());
			documentoComprobantePago.setNombre(doc.getNombre());
			documentoComprobantePago.setCompetidor(c);
			documentosC.add(documentoComprobantePago);
			c.setDocumentos(documentosC);
		}

		// Actualizar el estado del competidor
		if (tieneFichaInscripcion) {
			c.setEstadoParticipacion("Preinscrito");
		} else {
			c.setEstadoParticipacion("Pendiente");
		}

		// Guardar el competidor actualizado en el repositorio
		this.competidorRepo.actualizarCompetidor(c);

	}

	/**
	* Registra una ficha de inscripción para un competidor.
	* Una vez subido el pago y la ficha de inscripción el estado del competidor cambia a "Preinscrito"
	* @param doc DTO que contiene los detalles del documento a registrar  
	*/
	@Override
	public void registrarFichaInscripcion(DocsCompetidoresDTO doc) {

		// Obtener el competidor
		Competidor c = this.competidorRepo.buscarCompetidor(doc.getIdCompetidor());

		// Obtener documentos del competidor
		List<DocumentoCompetidores> documentosC = c.getDocumentos();

		// Verificar si ya existe el comprobante de pago
		boolean tieneComprobantePago = documentosC.stream()
			.anyMatch(docComp -> docComp.getNombre().startsWith("comprobante-pago"));

		// Verificar si ya existe la ficha de inscripción
		boolean tieneFichaInscripcion = documentosC.stream()  
			.anyMatch(docComp -> docComp.getNombre().startsWith("ficha-inscripcion"));

		// Actualizar documentos según existencia
		if (tieneFichaInscripcion) {

			// Obtener la ficha de inscripción existente
			Optional<DocumentoCompetidores> documentoFichaInscripcionOptional = documentosC.stream()
			.filter(docComp -> docComp.getNombre().startsWith("ficha-inscripcion"))
			.findFirst();

			DocumentoCompetidores documentoFichaInscripcion = documentoFichaInscripcionOptional.get();

			// Actualizar detalles de la ficha de inscripción
			documentoFichaInscripcion.setExtension(doc.getExtension());
			documentoFichaInscripcion.setLink(doc.getLink());  
			documentoFichaInscripcion.setNombre(doc.getNombre());

			// Actualizar ficha en el repositorio
			this.documentosCompetidoresRepo.actualizarDocumento(documentoFichaInscripcion);

		} else {

			// La ficha no existe, crear una nueva
			DocumentoCompetidores documentoFichaInscripcion = new DocumentoCompetidores();
			documentoFichaInscripcion.setExtension(doc.getExtension());
			documentoFichaInscripcion.setLink(doc.getLink());
			documentoFichaInscripcion.setNombre(doc.getNombre());
			documentoFichaInscripcion.setCompetidor(c);
			
			// Agregar nueva ficha a documentos del competidor
			documentosC.add(documentoFichaInscripcion);
			c.setDocumentos(documentosC);

		}

		// Actualizar estado participación del competidor
		if (tieneComprobantePago) {
			c.setEstadoParticipacion("Preinscrito");
		} else {
			c.setEstadoParticipacion("Pendiente");
		}  

		// Actualizar competidor en el repositorio
		this.competidorRepo.actualizarCompetidor(c);
	}

	/**
	* Confirma la inscripción de un competidor, cambia el estado a "Inscrito".
	*
	* @param id el id del competidor a confirmar inscripción
	* @throws Exception si el competidor no cumple requisitos para confirmar inscripción
	*/  
	@Override
	public void confirmarInscripcionCompetidor(Integer id) throws Exception {

		// Obtener el competidor
		Competidor c = this.competidorRepo.buscarCompetidor(id);

		// Obtener documentos del competidor
		List<DocumentoCompetidores> documentosC = c.getDocumentos();

		// Verificar si tiene inscripción firmada
		boolean tieneInscripcionFirmada = documentosC.stream()
			.anyMatch(docComp -> docComp.getNombre().startsWith("inscripcion-firmada"));

		// Confirmar inscripción si tiene inscripción firmada y pago aceptado
		if (tieneInscripcionFirmada && c.getEstadoParticipacion().equals("Pago Aceptado")) {
			
			// Cambiar estado a inscrito
			c.setEstadoParticipacion("Inscrito");
			
			// Actualizar competidor en el repositorio
			this.competidorRepo.actualizarCompetidor(c);

		} else {

			// Lanzar excepción si no cumple requisitos
			throw new Exception("El competidor no cumple con los requisitos para confirmar la inscripción");

		}
	}

	/**
	* Confirma el pago de un competidor.
	* Cambia el estado d "Pago Aceptado"
	* @param id el id del competidor a confirmar pago
	*/
	@Override
	public void confirmarPago(Integer id) {

		// Obtener el competidor
		Competidor c = this.competidorRepo.buscarCompetidor(id);

		// Cambiar estado a pago aceptado
		c.setEstadoParticipacion("Pago Aceptado");

		// Actualizar competidor en el repositorio
		this.competidorRepo.actualizarCompetidor(c);
	}

	/**
	* Niega el pago de un competidor.
	* Cambia es estado a "Pago Denegado"
	* @param id el id del competidor a negar pago
	*/
	@Override  
	public void negarPago(Integer id) {

		// Obtener el competidor
		Competidor c = this.competidorRepo.buscarCompetidor(id);

		// Cambiar estado a pago denegado
		c.setEstadoParticipacion("Pago Denegado");

		// Actualizar competidor en el repositorio
		this.competidorRepo.actualizarCompetidor(c);

	}

	/**
	* Aprueba la ficha de inscripción de un competidor.
	* Registra la inscripción firmada por un administrador
	* @param doc DTO con los detalles del documento de ficha firmada
	*/
	@Override
	public void aprobarFichaInscripcion(DocsCompetidoresDTO doc) {

		// Obtener el competidor
		Competidor c = this.competidorRepo.buscarCompetidor(doc.getIdCompetidor());

		// Obtener documentos del competidor
		List<DocumentoCompetidores> documentosC = c.getDocumentos();

		// Verificar si ya tiene ficha firmada
		boolean tieneFichaFirmada = documentosC.stream()
			.anyMatch(docComp -> docComp.getNombre().startsWith("inscripcion-firmada"));

		if (tieneFichaFirmada) {

			// Obtener ficha firmada existente
			Optional<DocumentoCompetidores> documentoFichaFirmadaOptional = documentosC.stream()
			.filter(docComp -> docComp.getNombre().startsWith("inscripcion-firmada"))
			.findFirst();

			DocumentoCompetidores documentoFichaFirmada = documentoFichaFirmadaOptional.get();

			// Actualizar detalles de ficha firmada
			documentoFichaFirmada.setExtension(doc.getExtension());
			documentoFichaFirmada.setLink(doc.getLink());
			documentoFichaFirmada.setNombre(doc.getNombre());

			// Actualizar ficha firmada en repo
			this.documentosCompetidoresRepo.actualizarDocumento(documentoFichaFirmada);

		} else {

			// Crear nueva ficha firmada
			DocumentoCompetidores documentoFichaFirmada = new DocumentoCompetidores();
			// establecer detalles
			documentosC.add(documentoFichaFirmada);
			c.setDocumentos(documentosC);

		}

		// Actualizar competidor en repo
		this.competidorRepo.actualizarCompetidor(c);
	}

	/**
	* Niega la inscripción de un competidor.
	* Cambia el estado a "Negado"
	* @param id el id del competidor a negar inscripción
	*/
	@Override
	public void negarInscripcionCompetidor(Integer id) {

		// Obtener el competidor
		Competidor c = this.competidorRepo.buscarCompetidor(id);

		// Cambiar estado a negado
		c.setEstadoParticipacion("Negado");

		// Actualizar competidor en el repo
		this.competidorRepo.actualizarCompetidor(c);

	}

	/**
	* Obtiene la ficha de inscripción de un competidor.
	*
	* @param idCompetidor el id del competidor
	* @return la ficha de inscripción 
	*/
	@Override
	public FichaInscripcionCampTO obtenerFichaInscripcion(Integer idCompetidor) {

		// Crear ficha vacía
		FichaInscripcionCampTO ficha = new FichaInscripcionCampTO();

		// Obtener competidor
		Competidor c = this.competidorRepo.buscarCompetidor(idCompetidor);

		// Obtener datos de usuario
		Usuario u = c.getUsuario();

		// Asignar datos a la ficha
		ficha.setApellidos(u.getApellidos());
		//...

		// Calcular edad y categoría
		Integer edad = this.calcularEdad(u.getFechaNacimiento());
		ficha.setCategoria(this.calcularCategoriaUsuario(edad, u.getSexo()));

		// Obtener campeonato y pruebas
		Campeonato camp = c.getCampeonatos().get(0);
		//...

		return ficha;
	}

	/**
	* Calcula la categoría de competición de un participante según su edad y género.
	* 
	* @param edad la edad del participante
	* @param genero el género del participante (M, F) 
	* @return la categoría calculada, null si ocurre error
	*/
	@Override
	public String calcularCategoriaUsuario(Integer edad, String genero) {

		String g = new String();

		if ("Masculino".equalsIgnoreCase(genero)) {
			g = "M";
		} else if ("Femenino".equalsIgnoreCase(genero)) {
			g = "F";
		} else {
			g = genero;
		}

		try {
			Categoria c = this.categoriaRepo.obtenerCategoriaPorEdadYGenero(edad, g);
			String cat = c.getNombre() + "-" + g;

			return cat;
		} catch (Exception e) {
			return null;
		}

	}

	// Lista competidores inscritos-----------------------------------------------------------------

	/**
	* Obtiene la lista de competidores inscritos.
	* 
	* @return lista de competidores inscritos 
	*/
	@Override
	public List<CompetidorResTO> listaCompetidoresInscritos() {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidoresInscritos();
		List<CompetidorResTO> comps = new ArrayList<CompetidorResTO>();

		if (competidores != null && !competidores.isEmpty()) {
			for (Competidor c : competidores) {
				CompetidorResTO com = new CompetidorResTO();
				com.setApellidos(c.getUsuario().getApellidos());
				com.setEmail(c.getUsuario().getEmail());
				com.setFechaInscripcion(c.getFechaInscripcion());
				com.setNombres(c.getUsuario().getNombres());
				com.setId(c.getId());
				com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
				com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
				com.setEstadoParticipacion(c.getEstadoParticipacion());
				List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());

				if (docs != null && !docs.isEmpty()) {
					List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
					com.setDocumentos(docsR);
				}

				List<Prueba> pruebas = c.getPruebas();
				if (pruebas != null && !pruebas.isEmpty()) {
					List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

					for (Prueba p : pruebas) {
						prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
					}

					com.setPruebas(prs);

				}

				comps.add(com);

			}
		}
		return comps;
	}

	/**
	* Obtiene la lista de competidores inscritos de un usuario.
	*
	* @param email el email del usuario
	* @return la lista de competidores inscritos
	*/
	@Override
	public List<CompetidorResTO> listaCompetidoresInscritosPorUsuario(String email) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidoresInscritosPorUsuario(email);
		List<CompetidorResTO> comps = new ArrayList<CompetidorResTO>();

		if (competidores != null && !competidores.isEmpty()) {
			for (Competidor c : competidores) {
				CompetidorResTO com = new CompetidorResTO();
				com.setApellidos(c.getUsuario().getApellidos());
				com.setEmail(c.getUsuario().getEmail());
				com.setFechaInscripcion(c.getFechaInscripcion());
				com.setNombres(c.getUsuario().getNombres());
				com.setId(c.getId());
				com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
				com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
				com.setEstadoParticipacion(c.getEstadoParticipacion());

				List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());
				if (docs != null && !docs.isEmpty()) {
					List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
					com.setDocumentos(docsR);
				}

				List<Prueba> pruebas = c.getPruebas();
				if (pruebas != null && !pruebas.isEmpty()) {
					List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

					for (Prueba p : pruebas) {
						prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
					}

					com.setPruebas(prs);

				}

				comps.add(com);

			}
		}

		return comps;
	}

	/**
	* Obtiene la lista de competidores inscritos en un campeonato por asociación.
	* 
	* @param idCampeonato el id del campeonato
	* @param idAsociacion el id de la asociación
	* @return la lista de competidores inscritos
	*/
	@Override
	public List<CompetidorResTO> listaCompetidoresInscritosPorCampeonato(Integer idCampeonato, Integer idAsociacion) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidoresInscritosPorCampeonato(idCampeonato, idAsociacion);
		List<CompetidorResTO> comps = new ArrayList<CompetidorResTO>();

		if (competidores != null && !competidores.isEmpty()) {
			for (Competidor c : competidores) {
				CompetidorResTO com = new CompetidorResTO();
				com.setApellidos(c.getUsuario().getApellidos());
				com.setEmail(c.getUsuario().getEmail());
				com.setFechaInscripcion(c.getFechaInscripcion());
				com.setNombres(c.getUsuario().getNombres());
				com.setId(c.getId());
				com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
				com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
				com.setEstadoParticipacion(c.getEstadoParticipacion());

				List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());
				if (docs != null && !docs.isEmpty()) {
					List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
					com.setDocumentos(docsR);
				}

				List<Prueba> pruebas = c.getPruebas();
				if (pruebas != null && !pruebas.isEmpty()) {
					List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

					for (Prueba p : pruebas) {
						prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
					}

					com.setPruebas(prs);

				}
				comps.add(com);

			}
		}

		return comps;
	}

	@Override
	public List<CompetidorResTO> listaCompetidoresPorCampeonatoUser(String email) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidorPorUsuario(email);
		List<CompetidorResTO> comps = new ArrayList<CompetidorResTO>();

		if (competidores != null && !competidores.isEmpty()) {
			for (Competidor c : competidores) {
				CompetidorResTO com = new CompetidorResTO();
				com.setApellidos(c.getUsuario().getApellidos());
				com.setEmail(c.getUsuario().getEmail());
				com.setFechaInscripcion(c.getFechaInscripcion());
				com.setNombres(c.getUsuario().getNombres());
				com.setId(c.getId());
				com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
				com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
				com.setEstadoParticipacion(c.getEstadoParticipacion());

				List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());
				if (docs != null && !docs.isEmpty()) {
					List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
					com.setDocumentos(docsR);
				}

				List<Prueba> pruebas = c.getPruebas();
				if (pruebas != null && !pruebas.isEmpty()) {
					List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

					for (Prueba p : pruebas) {
						prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
					}

					com.setPruebas(prs);

				}

				comps.add(com);

			}
		}

		return comps;
	}

	/**
	 * Recupera y devuelve información detallada sobre un competidor en un campeonato para un usuario dado.
	 *
	 * @param email El correo electrónico del usuario.
	 * @param idCampeonato El identificador del campeonato.
	 * @return Un objeto CompetidorResTO que contiene la información detallada del competidor.
	 */
	@Override
	public CompetidorResTO competidororCampeonatoUser(String email, Integer idCampeonato) {

		Competidor c = this.competidorRepo.buscarCompetidoresPorUserYCamp(email, idCampeonato);

		// Crear un objeto CompetidorResTO para almacenar la información del competidor
		CompetidorResTO com = new CompetidorResTO();
		com.setApellidos(c.getUsuario().getApellidos());
		com.setEmail(c.getUsuario().getEmail());
		com.setFechaInscripcion(c.getFechaInscripcion());
		com.setNombres(c.getUsuario().getNombres());
		com.setId(c.getId());
		com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
		com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
		com.setEstadoParticipacion(c.getEstadoParticipacion());

		// Recuperar documentos asociados al competidor
		List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());
		if (docs != null && !docs.isEmpty()) {
			List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
			com.setDocumentos(docsR);
		}

		// Recuperar pruebas asociadas al competidor
		List<Prueba> pruebas = c.getPruebas();
		if (pruebas != null && !pruebas.isEmpty()) {
			List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

			for (Prueba p : pruebas) {
				prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
			}

			com.setPruebas(prs);

		}

		return com;

	}

	/**
	 * Recupera y devuelve una lista de información detallada sobre los competidores inscritos en un campeonato para un usuario dado.
	 *
	 * @param email El correo electrónico del usuario.
	 * @param idCampeonato El identificador del campeonato.
	 * @return Una lista de objetos CompetidorResTO que contiene la información detallada de los competidores inscritos.
	 */
	@Override
	public List<CompetidorResTO> listaCompetidoresInscritosPorCampeonatoUser(String email, Integer idCampeonato) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidorresInscritosPorUserYCamp(email,
				idCampeonato);
		List<CompetidorResTO> comps = new ArrayList<CompetidorResTO>();

		// Verificar si hay competidores para evitar NullPointerException
		if (competidores != null && !competidores.isEmpty()) {
			for (Competidor c : competidores) {
				CompetidorResTO com = new CompetidorResTO();
				com.setApellidos(c.getUsuario().getApellidos());
				com.setEmail(c.getUsuario().getEmail());
				com.setFechaInscripcion(c.getFechaInscripcion());
				com.setNombres(c.getUsuario().getNombres());
				com.setId(c.getId());
				com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
				com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
				com.setEstadoParticipacion(c.getEstadoParticipacion());

				// Recuperar documentos asociados al competidor
				List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());
				if (docs != null && !docs.isEmpty()) {
					List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
					com.setDocumentos(docsR);
				}

				// Recuperar pruebas asociadas al competidor
				List<Prueba> pruebas = c.getPruebas();
				if (pruebas != null && !pruebas.isEmpty()) {
					List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

					for (Prueba p : pruebas) {
						prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
					}

					com.setPruebas(prs);

				}

				comps.add(com);

			}
		}
		return comps;
	}

	/**
	 * Busca y devuelve la información requerida de un competidor según su identificador.
	 *
	 * @param id El identificador del competidor.
	 * @return Un objeto CompetidorReqTO que contiene la información requerida del competidor.
	 */
	@Override
	public CompetidorReqTO buscarCompetidorID(Integer id) {
		Competidor competidor = this.competidorRepo.buscarCompetidor(id);
		CompetidorReqTO c = new CompetidorReqTO();
		c.setEmail(competidor.getUsuario().getEmail());
		c.setIdAsociacionDeportiva(competidor.getAsociacionDeportiva().getId());
		c.setIdCampeonato(id);

		List<Prueba> pruebas = competidor.getPruebas();

		List<Integer> idsP = new ArrayList<Integer>();

		for (Prueba p : pruebas) {
			Integer n = p.getId();
			idsP.add(n);
		}
		c.setPruebas(idsP);
		return c;

	}

	/**
	 * Recupera y devuelve una lista de información detallada sobre los competidores asociados a un usuario.
	 *
	 * @param email El correo electrónico del usuario.
	 * @return Una lista de objetos CompetidorResTO que contiene la información detallada de los competidores.
	 */
	@Override
	public List<CompetidorResTO> listaCompetidoresPorUsuario(String email) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidorPorUsuario(email);
		List<CompetidorResTO> comps = new ArrayList<CompetidorResTO>();

		// Verificar si hay competidores para evitar NullPointerException
		if (competidores != null && !competidores.isEmpty()) {
			for (Competidor c : competidores) {
				CompetidorResTO com = new CompetidorResTO();
				com.setApellidos(c.getUsuario().getApellidos());
				com.setEmail(c.getUsuario().getEmail());
				com.setFechaInscripcion(c.getFechaInscripcion());
				com.setNombres(c.getUsuario().getNombres());
				com.setId(c.getId());
				com.setNombreCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getNombre());
				com.setIdCampeonato(c.getCampeonatos().get(c.getCampeonatos().size() - 1).getId());
				com.setEstadoParticipacion(c.getEstadoParticipacion());

				// Recuperar documentos asociados al competidor
				List<DocumentoCompetidores> docs = this.competidorRepo.buscarDocsCompetidores(c.getId());
				if (docs != null && !docs.isEmpty()) {
					List<DocResponseDTO> docsR = this.azureBlobAdapter.listarDocumentosCompetidor(docs);
					com.setDocumentos(docsR);
				}

				// Recuperar pruebas asociadas al competidor
				List<Prueba> pruebas = c.getPruebas();
				if (pruebas != null && !pruebas.isEmpty()) {
					List<PruebaResponseDTO> prs = new ArrayList<PruebaResponseDTO>();

					for (Prueba p : pruebas) {
						prs.add(this.pruebaService.convertirPruebaResponseDTO(p));
					}

					com.setPruebas(prs);

				}

				comps.add(com);

			}
		}

		return comps;
	}

	/**
	 * Calcula y devuelve la edad a partir de la fecha de nacimiento.
	 *
	 * @param fechaNacimiento La fecha de nacimiento.
	 * @return La edad calculada en años.
	 * @throws IllegalArgumentException Si la fecha de nacimiento es nula.
	 */
	private int calcularEdad(LocalDateTime fechaNacimiento) {
		if (fechaNacimiento == null) {
			throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
		}

		LocalDateTime fechaActual = LocalDateTime.now();
		LocalDate nacimientoLocalDate = fechaNacimiento.toLocalDate();
		LocalDate actualLocalDate = fechaActual.toLocalDate();

		Period periodo = Period.between(nacimientoLocalDate, actualLocalDate);
		System.out.println(periodo.getYears());
		return periodo.getYears();
	}

	/**
	 * Formatea las fechas de inicio y fin en un formato específico.
	 *
	 * @param fechaInicio La fecha de inicio.
	 * @param fechaFin La fecha de fin.
	 * @return Una cadena formateada con las fechas.
	 */
	private String formatearFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		DateTimeFormatter formatterDia = DateTimeFormatter.ofPattern("d", new Locale("es", "ES"));
		DateTimeFormatter formatterMes = DateTimeFormatter.ofPattern("MMMM", new Locale("es", "ES"));
		DateTimeFormatter formatterAnio = DateTimeFormatter.ofPattern("yyyy");

		String fechaInicioStr = fechaInicio.format(formatterDia) + " de " + fechaInicio.format(formatterMes);
		String fechaFinStr = fechaFin.format(formatterDia) + " de " + fechaFin.format(formatterMes);
		String anioInicioStr = fechaInicio.format(formatterAnio);
		String anioFinStr = fechaFin.format(formatterAnio);

		if (fechaInicio.getMonth().equals(fechaFin.getMonth())) {
			// Mismo mes
			if (fechaInicio.getYear() == fechaFin.getYear()) {
				// Mismo año
				return fechaInicioStr + " y " + fechaFinStr + " de " + anioInicioStr;
			} else {
				// Años diferentes
				return fechaInicioStr + " de " + anioInicioStr + " y " + fechaFinStr + " de " + anioFinStr;
			}
		} else {
			// Meses diferentes
			return fechaInicioStr + " de " + anioInicioStr + " y " + fechaFinStr + " de " + anioFinStr;
		}
	}

}
