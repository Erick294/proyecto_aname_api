package com.aname.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.Prueba;
import com.aname.api.repository.IPruebaRepo;
import com.aname.api.service.to.PruebaResponseDTO;

@Service
public class PruebasServiceImpl implements IPruebaService {

	@Autowired
	private IPruebaRepo pruebaRepo;

	@Override
	public List<PruebaResponseDTO> listarPruebas() {
		List<Prueba> pruebas = this.pruebaRepo.buscarTodosPrueba();
		List<PruebaResponseDTO> lista = new ArrayList<PruebaResponseDTO>();

		for (Prueba p : pruebas) {
			this.convertirPruebaResponseDTO(p);
			lista.add(this.convertirPruebaResponseDTO(p));
		}

		return lista;
	}
	
	@Override
	public List<PruebaResponseDTO> listarPruebasPorCampeonato(Integer idCampeonato) {
		List<Prueba> pruebas = this.pruebaRepo.buscarPruebasPorCampeonato(idCampeonato);
		List<PruebaResponseDTO> lista = new ArrayList<PruebaResponseDTO>();

		for (Prueba p : pruebas) {
			this.convertirPruebaResponseDTO(p);
			lista.add(this.convertirPruebaResponseDTO(p));
		}

		return lista;
	}
	
	@Override
	public List<PruebaResponseDTO> listarPruebasPorCampeonatoYCategoria(Integer idCampeonato, Integer idCategoria) {
		List<Prueba> pruebas = this.pruebaRepo.buscarPruebasPorCampeonatoYCategoria(idCampeonato, idCategoria);
		List<PruebaResponseDTO> lista = new ArrayList<PruebaResponseDTO>();

		for (Prueba p : pruebas) {
			this.convertirPruebaResponseDTO(p);
			lista.add(this.convertirPruebaResponseDTO(p));
		}

		return lista;
	}

	@Override
	public void actualizarPrueba(Prueba prueba) {
		this.pruebaRepo.actualizarPrueba(prueba);
	}

	@Override
	public Prueba buscarPrueba(Integer id) {
		return this.pruebaRepo.buscarPrueba(id);
	}

	@Override
	public Prueba convertirAPruebaResponseDTO(PruebaResponseDTO p) {
		Prueba pr = new Prueba();
		pr.setId(p.getId());
		pr.setIntentos(p.getIntentos());
		pr.setNombre(p.getNombre());
		pr.setTipo(p.getTipo());
		return pr;
	}

	@Override
	public PruebaResponseDTO convertirPruebaResponseDTO(Prueba p) {
		PruebaResponseDTO pr = new PruebaResponseDTO();
		pr.setId(p.getId());
		pr.setIntentos(p.getIntentos());
		pr.setNombre(p.getNombre());
		pr.setTipo(p.getTipo());
		return pr;
	}

}
