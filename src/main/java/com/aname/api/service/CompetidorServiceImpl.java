package com.aname.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.AsociacionDeportiva;
import com.aname.api.model.Campeonato;
import com.aname.api.model.Categoria;
import com.aname.api.model.Competidor;
import com.aname.api.model.DocumentoCompetidores;
import com.aname.api.model.PrecioInscripcion;
import com.aname.api.model.Prueba;
import com.aname.api.model.Usuario;
import com.aname.api.repository.ICategoriaRepo;
import com.aname.api.repository.ICompetidorRepo;
import com.aname.api.repository.IPrecioInscripcionRepo;
import com.aname.api.service.to.CategoriaTO;
import com.aname.api.service.to.CompetidorReqTO;
import com.aname.api.service.to.CompetidorResTO;
import com.aname.api.service.to.DocResponseDTO;
import com.aname.api.service.to.FichaInscripcionCampTO;
import com.aname.api.service.to.InscripcionDocsReq;
import com.aname.api.service.to.PreciosInscripcionCalcTO;
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
	public FichaInscripcionCampTO obtenerFichaInscripcion(String email, Integer idcampeonato) {
		FichaInscripcionCampTO ficha = new FichaInscripcionCampTO();
		Usuario u = this.usuarioService.buscarUsuarioPorEmail(email);

		ficha.setApellidos(u.getApellidos());
		ficha.setCiudad(u.getCiudad());
		ficha.setDireccion(u.getCiudad());
		ficha.setEmail(email);
		ficha.setFechaNacimiento(u.getFechaNacimiento());
		ficha.setNombres(u.getNombres());
		ficha.setSexo(u.getSexo());

		List<Integer> idsAsocUsua = new ArrayList<Integer>();

		for (AsociacionDeportiva a : u.getAsociaciones()) {
			idsAsocUsua.add(a.getId());
		}

		ficha.setAsociacionesUsuariosIds(idsAsocUsua);

		Integer edad = this.calcularEdad(u.getFechaNacimiento());

		ficha.setCategoria(this.calcularCategoriaUsuario(edad, u.getSexo()));

		PrecioInscripcion pre = this.precioInscripcionRepo.buscarPreciosPorCampeonato(idcampeonato);
		ficha.setCostoNoSocio(pre.getCostoNoSocio());
		ficha.setCostoPruebaAdicional(pre.getCostoPruebaAdicional());
		ficha.setCostoSocio(pre.getCostoSocio());
		ficha.setCuentaBancaria(pre.getCuentaBancaria());

		// ficha.setPruebas(this.pruebaService.listarPruebasPorCampeonato(idcampeonato));
		ficha.setPruebas(
				this.pruebaService.listarPruebasPorCampeonatoYCategoria(idcampeonato, ficha.getCategoria().getId()));

		return ficha;

	}

	@Override
	public PreciosInscripcionCalcTO calcularPreciosInscripcion(Integer idCampeonato, String email,
			List<String> pruebas) {

		PrecioInscripcion p = this.precioInscripcionRepo.buscarPreciosPorCampeonato(idCampeonato);

		Usuario u = this.usuarioService.buscarUsuarioPorEmail(email);

		PreciosInscripcionCalcTO precioI = new PreciosInscripcionCalcTO();

		BigDecimal total = new BigDecimal(0);
		BigDecimal precioSocio = p.getCostoSocio();
		BigDecimal precioNoSocio = p.getCostoNoSocio();
		BigDecimal precioPruAd = p.getCostoPruebaAdicional();

		Integer numPruebas = (int) pruebas.stream().count();
		System.out.println("NumP: " + numPruebas);

		Integer pruebasAdicionales = 0;

		// Ajuste para pruebas adicionales si hay 4 o más pruebas
		if (numPruebas >= 4) {
			pruebasAdicionales = numPruebas - 3;
		}

		precioI.setPrecioPentatlon(BigDecimal.ZERO); // Inicializar el precio como 0

		for (int i = 0; i < pruebas.size(); i++) {
			String pn = pruebas.get(i);
			System.out.println("Prueba: " + pn + " i: " + i);

			if (pn.toLowerCase().contains("pentatlon")) {
				numPruebas--;
				precioI.setPrecioPentatlon(new BigDecimal(10));
				total = total.add(precioI.getPrecioPentatlon());

				System.out.println("NumP sin Pent: " + numPruebas);
			}

			if (pn.toLowerCase().contains("posta")) {
				numPruebas--;
			}
		}

		// Ajuste para pruebas adicionales si hay 4 o más pruebas
		if (numPruebas >= 4) {
			pruebasAdicionales = numPruebas - 3;
			System.out.println("Pruebas Adicionales: " + pruebasAdicionales);
		} else {
			pruebasAdicionales = 0;
		}

		System.out.println("Preubas finales" + pruebasAdicionales);

		// System.out.println("Asociaciones: " + u.getAsociaciones());
		if (u.getAsociaciones() != null && !u.getAsociaciones().isEmpty()) {
			total = total.add(precioSocio);
			System.out.println("TOTALS: " + total);

			precioI.setPrecioSocio(precioSocio);
			precioI.setPrecioNoSocio(BigDecimal.ZERO);
		} else {
			total = total.add(precioNoSocio);
			System.out.println("TOTALNS: " + total);
			precioI.setPrecioNoSocio(precioSocio);
			precioI.setPrecioSocio(BigDecimal.ZERO);
		}

		BigDecimal totalAdicional = precioPruAd.multiply(new BigDecimal(pruebasAdicionales));
		total = total.add(totalAdicional);

		precioI.setPrecioPruebasAdicionales(totalAdicional);
		precioI.setTotal(total);

		return precioI;
	}

	@Override
	public CategoriaTO calcularCategoriaUsuario(Integer edad, String genero) {

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

		Categoria c = this.categoriaRepo.obtenerCategoriaPorEdadYGenero(edad, g);
		CategoriaTO cat = new CategoriaTO();

		cat.setId(c.getId());
		cat.setNombre(c.getNombre() + "-" + g);
		return cat;

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

}
