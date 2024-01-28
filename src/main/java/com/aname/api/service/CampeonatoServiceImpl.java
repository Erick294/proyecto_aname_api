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

		// precio.setCampeonato(c);

		// this.precioInscripcionRepo.actualizarPrecioInscripcion(precio);

	}

	@Override
	public Campeonato buscarCampeonatoPorID(Integer id) {
		return this.campeonatoRepo.buscarCampeonato(id);
	}

	@Override
	public List<CampeonatoReqDTO> listarCampeonatosDisponibles() {
		List<Campeonato> campeonatos = this.campeonatoRepo.buscarCampeonatosDisponibles();

		List<CampeonatoReqDTO> campsR = new ArrayList<CampeonatoReqDTO>();

		for (Campeonato c : campeonatos) {
			CampeonatoReqDTO campeonato = this.convertirACampeonatoReqDTO(c);
			campsR.add(campeonato);
		}

		return campsR;

	}

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

	private Campeonato convertirCampeonatoReqDTO(CampeonatoReqDTO campeonato) {
		Campeonato c = new Campeonato();
		// c.setId(campeonato.getId());
		c.setFechaFin(campeonato.getFechaFin());
		c.setFechaInicio(campeonato.getFechaInicio());
		c.setNombre(campeonato.getNombre());
		c.setOrganizador(campeonato.getOrganizador());
		c.setSede(campeonato.getSede());
		c.setInscripcionFin(campeonato.getInscripcionFin());
		c.setInscripcionInicio(campeonato.getInscripcionInicio());

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

		if (campeonato.getPruebas() != null && !campeonato.getPruebas().isEmpty()) {
			List<Integer> pruebas = new ArrayList<Integer>();

			for (Prueba p : campeonato.getPruebas()) {
				Integer id = p.getId();
				pruebas.add(id);
			}

			c.setPruebas(pruebas);
		}

		if (campeonato.getPrecioInscripcion() != null) {
			c.setCostoNoSocio(campeonato.getPrecioInscripcion().getCostoNoSocio());
			c.setCostoPruebaAdicional(campeonato.getPrecioInscripcion().getCostoPruebaAdicional());
			c.setCostoSocio(campeonato.getPrecioInscripcion().getCostoSocio());
			c.setTitularCuenta(campeonato.getPrecioInscripcion().getTitularCuenta());
			c.setInstitucionFinanciera(campeonato.getPrecioInscripcion().getInstitucionFinanciera());
		}
		// PrecioInscripcion precio = new PrecioInscripcion();

		return c;

	}

}
