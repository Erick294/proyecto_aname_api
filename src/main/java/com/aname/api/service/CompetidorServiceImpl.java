package com.aname.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.aname.api.repository.IPrecioInscripcionRepo;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.CompetidorResTO;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.FichaInscripcionCampTO;
import com.aname.api.service.to.InscripcionDocsReq;
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

	@Override
	public void registroInicialCompetidor(CompetidorReqTO c) {
		Competidor competidor = new Competidor();

		competidor.setAsociacionDeportiva(
				this.asociacionDeportivaService.buscarAsociacionDeportiva(c.getIdAsociacionDeportiva()));

		List<Campeonato> campeonatos = new ArrayList<Campeonato>();
		campeonatos.add(this.campeonatoService.buscarCampeonatoPorID(c.getIdCampeonato()));

		competidor.setCampeonatos(campeonatos);
		competidor.setEstadoParticipacion("Pendiente");
		competidor.setFechaInscripcion(LocalDateTime.now());

		List<Prueba> pruebas = new ArrayList<Prueba>();
		for (Integer id : c.getPruebas()) {
			pruebas.add(this.pruebaService.buscarPrueba(id));
		}
		competidor.setPruebas(pruebas);
		competidor.setUsuario(this.usuarioService.buscarUsuarioPorEmail(c.getEmail()));

		this.competidorRepo.insertarCompetidor(competidor);
	}

	@Override
	public void inscripcionCompleta(InscripcionDocsReq i) {
		Competidor comp = this.competidorRepo.buscarCompetidorPorUserYCamp(i.getEmail(), i.getIdCampeonato());

		comp.setEstadoParticipacion("Inscrito");

		DocumentoCompetidores pago = new DocumentoCompetidores();
		pago.setExtension(i.getComprobantePago().getExtension());
		pago.setLink(i.getComprobantePago().getLink());
		pago.setNombre(i.getComprobantePago().getNombre());
		pago.setCompetidor(comp);

		DocumentoCompetidores ficha = new DocumentoCompetidores();
		ficha.setExtension(i.getFichaInscripcion().getExtension());
		ficha.setLink(i.getFichaInscripcion().getLink());
		ficha.setNombre(i.getFichaInscripcion().getNombre());
		ficha.setCompetidor(comp);

		List<DocumentoCompetidores> documentos = comp.getDocumentos();
		documentos.add(pago);
		documentos.add(ficha);

		comp.setDocumentos(documentos);

		this.competidorRepo.actualizarCompetidor(comp);
	}

	// Buscar competidor por id

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

	// Lista de competidores (todos los estados por user)
	@Override
	public List<CompetidorResTO> listaCompetidoresPorUsuario(String email) {

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

	// Lista competidores
	// inscritos-----------------------------------------------------------------

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

	@Override
	public List<CompetidorResTO> listaCompetidoresInscritosPorCampeonato(Integer idCampeonato) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidoresInscritosPorCampeonato(idCampeonato);
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

	@Override
	public CompetidorResTO competidororCampeonatoUser(String email, Integer idCampeonato) {

		Competidor c = this.competidorRepo.buscarCompetidoresPorUserYCamp(email, idCampeonato);

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

		return com;

	}

	@Override
	public List<CompetidorResTO> listaCompetidoresInscritosPorCampeonatoUser(String email, Integer idCampeonato) {

		List<Competidor> competidores = this.competidorRepo.buscarCompetidorresInscritosPorUserYCamp(email,
				idCampeonato);
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
	public void confirmarInscripcionCompetidor(Integer id) {
		Competidor c = this.competidorRepo.buscarCompetidor(id);
		c.setEstadoParticipacion("Confirmado");
		this.competidorRepo.actualizarCompetidor(c);
	}

	@Override
	public void negarInscripcionCompetidor(Integer id) {
		Competidor c = this.competidorRepo.buscarCompetidor(id);
		c.setEstadoParticipacion("Negado");
		// c.setCampeonatos(null);
		this.competidorRepo.actualizarCompetidor(c);
	}

	@Override
	public FichaInscripcionCampTO obtenerFichaInscripcion(Integer idCompetidor) {
		FichaInscripcionCampTO ficha = new FichaInscripcionCampTO();
		Competidor c = this.competidorRepo.buscarCompetidor(idCompetidor);

		Usuario u = c.getUsuario();

		ficha.setApellidos(u.getApellidos());
		ficha.setCiudad(u.getCiudad());
		ficha.setDireccion(u.getCiudad());
		ficha.setEmail(u.getEmail());
		ficha.setFechaNacimiento(u.getFechaNacimiento());
		ficha.setNombres(u.getNombres());
		ficha.setSexo(u.getSexo());

		Integer edad = this.calcularEdad(u.getFechaNacimiento());
		ficha.setCategoria(this.calcularCategoriaUsuario(edad, u.getSexo()));

		ficha.setAsociacion(c.getAsociacionDeportiva().getNombre());

		Campeonato camp = c.getCampeonatos().get(0);

		ficha.setFechaCampeonato(this.formatearFechas(camp.getFechaInicio(), camp.getFechaFin()));
		ficha.setNombreCampeonato(camp.getNombre());
		
		List<Prueba> pruebas = c.getPruebas();
		List<PruebaResponseDTO> lista = new ArrayList<PruebaResponseDTO>();

		for (Prueba p : pruebas) {
			PruebaResponseDTO pr = this.pruebaService.convertirPruebaResponseDTO(p);
			lista.add(pr);
		}
		ficha.setPruebas(lista);

		return ficha;

	}

	@Override
	public String calcularCategoriaUsuario(Integer edad, String genero) {

		System.out.println("Genero: " + genero);

		String g = new String();

		if ("Masculino".equalsIgnoreCase(genero)) {
			g = "M";
		} else if ("Femenino".equalsIgnoreCase(genero)) {
			g = "F";
		} else {
			g = genero;
		}

		System.out.println("GeneroT: " + g);

		try {
			Categoria c = this.categoriaRepo.obtenerCategoriaPorEdadYGenero(edad, g);
			String cat = c.getNombre() + "-" + g;

			return cat;
		} catch (Exception e) {
			return null;
		}

	}

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
