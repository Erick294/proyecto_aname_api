package com.aname.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.AsociacionDeportiva;
import com.aname.api.model.Campeonato;
import com.aname.api.model.PrecioInscripcion;
import com.aname.api.model.Prueba;
import com.aname.api.repository.ICampeonatoRepo;
import com.aname.api.repository.IPrecioInscripcionRepo;
import com.aname.api.service.to.CampeonatoReqDTO;
import com.aname.api.service.to.DTOReporte1;
import com.aname.api.service.to.DTOReporte2;
import com.aname.api.service.to.PreciosCampeonatosTO;

@Service
public class CampeonatoServiceImpl implements ICampeonatoService {

	@Autowired
	private ICampeonatoRepo campeonatoRepo;

	@Autowired
	private IPruebaService pruebaService;

	@Autowired
	private IPrecioInscripcionRepo precioInscripcionRepo;

	@Autowired
	private IAsociacionDeportivaService asociacionDeportivaService;
	
	/**
	* Método para obtener los precios de un campeonato.
	* @param idCampeonato - Id del campeonato a obtener
	* @return Devolver un objeto PreciosCampeonatosTO con los precios del campeonato
	*/
	@Override
	public PreciosCampeonatosTO obtenerPreciosCampeonato(Integer idCampeonato) {
		PrecioInscripcion precio = this.precioInscripcionRepo.buscarPreciosPorCampeonato(idCampeonato);
		PreciosCampeonatosTO p = new PreciosCampeonatosTO();
		p.setCostoNoSocio(precio.getCostoNoSocio());
		p.setCostoPruebaAdicional(precio.getCostoPruebaAdicional());
		p.setCostoSocio(precio.getCostoSocio());
		p.setCuentaBancaria(precio.getCuentaBancaria());
		p.setInstitucionFinanciera(precio.getInstitucionFinanciera());
		p.setTitularCuenta(precio.getTitularCuenta());
		
		return p;
		
	}

	/**
	* Método de registro de un Campeonato en la base de datos
	* @param campeonato - Objeto que contiene los datos del campeonato
	*/
	@Override
	public void registrarCampeonato(CampeonatoReqDTO campeonato) {
		Campeonato c = this.convertirCampeonatoReqDTO(campeonato);

		this.campeonatoRepo.insertarCampeonato(c);
		PrecioInscripcion precio = new PrecioInscripcion();

		precio.setCostoNoSocio(campeonato.getCostoNoSocio());
		precio.setCostoPruebaAdicional(campeonato.getCostoPruebaAdicional());
		precio.setCostoSocio(campeonato.getCostoSocio());
		precio.setInstitucionFinanciera(campeonato.getInstitucionFinanciera());
		precio.setTitularCuenta(campeonato.getTitularCuenta());
		
		precio.setCampeonato(c);
		precio.setCuentaBancaria(campeonato.getCuentaBancaria());

		if (precio.getCostoNoSocio() != null) {
			this.precioInscripcionRepo.insertarPrecioInscripcion(precio);
			c.setPrecioInscripcion(precio);
		}

		AsociacionDeportiva a = this.asociacionDeportivaService.buscarAsociacionDeportiva(campeonato.getIdAsociacion());

		if (a != null) {
			if (a.getCampeonatos() != null) {
				List<Campeonato> camps = a.getCampeonatos();
				camps.add(c);
				a.setCampeonatos(camps);
			}else {
				List<Campeonato> camps = new ArrayList<Campeonato>();
				camps.add(c);
				a.setCampeonatos(camps);
			}

		}
		
		this.asociacionDeportivaService.actualizarAsociacion(a);
		this.campeonatoRepo.actualizarCampeonato(c);
	}

	/**
	* Busca un campeonato por id
	* @param id - Id del campeonato a buscar
	* @return Objeto Campeonato con la información especificada o nula si no existe
	*/
	@Override
	public Campeonato buscarCampeonatoPorID(Integer id) {
		return this.campeonatoRepo.buscarCampeonato(id);
	}

	/**
	* Método para devolver una lista de campeonatos disponibles
	* @return Lista de Campeonatos que haya encontrado
	*/
	@Override
	public List<CampeonatoReqDTO> listarCampeonatosDisponibles() {
		List<Campeonato> campeonatos = this.campeonatoRepo.buscarCampeonatosDisponibles();
		//System.out.println("Cuenta: " + campeonatos.size());

		List<CampeonatoReqDTO> campsR = new ArrayList<CampeonatoReqDTO>();

		for (Campeonato c : campeonatos) {
			CampeonatoReqDTO campeonato = this.convertirACampeonatoReqDTO(c);
			campsR.add(campeonato);
		}

		return campsR;

	}

	/**
	* Lista de todos los campeonatos que se encuentren en la base de datos
	* @return Lista con los campeonatos de la base de datos
	*/
	@Override
	public List<CampeonatoReqDTO> listarCampeonatos() {
		List<Campeonato> campeonatos = this.campeonatoRepo.buscarTodosCampeonato();

		List<CampeonatoReqDTO> campsR = new ArrayList<CampeonatoReqDTO>();

		for (Campeonato c : campeonatos) {
			CampeonatoReqDTO campeonato = this.convertirACampeonatoReqDTO(c);
			campsR.add(campeonato);
		}

		return campsR;

	}

	/**
	* Convierte un CampeonatoDTO a un objeto Campeonato
	* @param campeonato - Objeto que contiene los datos del campeonato
	* @return Campeonato transformado desde el DTO
	*/
	private Campeonato convertirCampeonatoReqDTO(CampeonatoReqDTO campeonato) {
		Campeonato c = new Campeonato();
		c.setFechaFin(campeonato.getFechaFin());
		c.setFechaInicio(campeonato.getFechaInicio());
		c.setNombre(campeonato.getNombre());
		c.setOrganizador(campeonato.getOrganizador());
		c.setSede(campeonato.getSede());
		c.setInscripcionFin(campeonato.getInscripcionFin());
		c.setInscripcionInicio(campeonato.getInscripcionInicio());

		// Setear las pruebas del campeonato
		if (campeonato.getPruebas() != null && !campeonato.getPruebas().isEmpty()) {
			List<Prueba> pruebas = new ArrayList<Prueba>();

			for (Integer p : campeonato.getPruebas()) {
				Prueba pr = this.pruebaService.buscarPrueba(p);
				List<Campeonato> camps = pr.getCampeonatos();
				camps.add(c);
				pr.setCampeonatos(camps);
				this.pruebaService.actualizarPrueba(pr);
				pruebas.add(pr);
			}

			c.setPruebas(pruebas);
		}

		return c;

	}

	/**
	* Convierte un Campeonato a DTO
	* @param campeonato - objeto que contiene la información del campeonato
	* @return CampeonatoDTO transformado a partir de Campeonato
	*/
	private CampeonatoReqDTO convertirACampeonatoReqDTO(Campeonato campeonato) {

		CampeonatoReqDTO c = new CampeonatoReqDTO();
		c.setId(campeonato.getId());
		c.setFechaFin(campeonato.getFechaFin());
		c.setFechaInicio(campeonato.getFechaInicio());
		c.setNombre(campeonato.getNombre());
		c.setOrganizador(campeonato.getOrganizador());
		c.setSede(campeonato.getSede());
		c.setInscripcionFin(campeonato.getInscripcionFin());
		c.setInscripcionInicio(campeonato.getInscripcionInicio());

		// Setear las pruebas del campeonato
		if (campeonato.getPruebas() != null && !campeonato.getPruebas().isEmpty()) {
			List<Integer> pruebas = new ArrayList<Integer>();

			for (Prueba p : campeonato.getPruebas()) {
				Integer id = p.getId();
				pruebas.add(id);
			}

			c.setPruebas(pruebas);
		}

		// Setear los precios de inscripcion
		if (campeonato.getPrecioInscripcion() != null) {
			c.setCostoNoSocio(campeonato.getPrecioInscripcion().getCostoNoSocio());
			c.setCostoPruebaAdicional(campeonato.getPrecioInscripcion().getCostoPruebaAdicional());
			c.setCostoSocio(campeonato.getPrecioInscripcion().getCostoSocio());
			c.setTitularCuenta(campeonato.getPrecioInscripcion().getTitularCuenta());
			c.setInstitucionFinanciera(campeonato.getPrecioInscripcion().getInstitucionFinanciera());
		}

		return c;


	}

	@Override
	public List<DTOReporte1> reporteUno(){
		List<Object[]> reporte = this.campeonatoRepo.findUsuariosWithCampeonatos();
	    List<DTOReporte1> nuevo = new ArrayList<>();

		for(Object[] fila : reporte){
			DTOReporte1 rd = new DTOReporte1();
			rd.setNombres((String) fila[0]);
			rd.setApellidos((String) fila[1]);
			rd.setCateNombre((String) fila[2]);
			rd.setGenero((String) fila[3]);
			rd.setProvincia((String) fila[4]);
			rd.setNombreCampeonato((String) fila[5]);
			nuevo.add(rd);

		}

		return nuevo; 

	}

	
	@Override
	public List<DTOReporte2> reporteDos(){
		List<Object[]> reporte = this.campeonatoRepo.findPruebasCampeonatos();
	    List<DTOReporte2> nuevo = new ArrayList<>();

		for(Object[] fila : reporte){
			DTOReporte2 rd = new DTOReporte2();
			rd.setCampNombre((String) fila[0]);
			rd.setPruebaNombre((String) fila[1]);
			rd.setUsuaNombres((String) fila[2]);
			rd.setUsuaApellidos((String) fila[3]);
			rd.setCateNombre((String) fila[4]);
			rd.setAsoNombre((String) fila[5]);
			rd.setCateGenero((String) fila[6]);
			nuevo.add(rd);

		}

		return nuevo; 

	}


}
