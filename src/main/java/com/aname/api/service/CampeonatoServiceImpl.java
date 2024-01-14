package com.aname.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.Campeonato;
import com.aname.api.model.Prueba;
import com.aname.api.repository.ICampeonatoRepo;
import com.aname.api.service.to.CampeonatoReqDTO;
import com.aname.api.service.to.PruebaResponseDTO;

@Service
public class CampeonatoServiceImpl implements ICampeonatoService{
	
	@Autowired
	private ICampeonatoRepo campeonatoRepo;
	
	@Autowired
	private IPruebaService pruebaService;

	@Override
	public void registrarCampeonato(CampeonatoReqDTO campeonato) {
		Campeonato c = this.convertirCampeonatoReqDTO(campeonato);
		System.out.println("Ingresando cam-----");
		this.campeonatoRepo.insertarCampeonato(c);
		
	}
	
	
	
	private Campeonato convertirCampeonatoReqDTO(CampeonatoReqDTO campeonato) {
		Campeonato c =new Campeonato();
		c.setFechaFin(campeonato.getFechaFin());
		c.setFechaInicio(campeonato.getFechaInicio());
		c.setNombre(campeonato.getNombre());
		c.setOrganizador(campeonato.getOrganizador());
		c.setSede(campeonato.getSede());
		c.setInscripcionFin(campeonato.getInscripcionFin());
		c.setInscripcionInicio(campeonato.getInscripcionInicio());
		
		List<Prueba> pruebas = new ArrayList<Prueba>();
		
		for(Integer p : campeonato.getPruebas()) {
			Prueba pr= this.pruebaService.buscarPrueba(p);
			this.pruebaService.actualizarPrueba(pr);
			pruebas.add(pr);
		}
		
		c.setPruebas(pruebas);
		return c;
		
	}

}
