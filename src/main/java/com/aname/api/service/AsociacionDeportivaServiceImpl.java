package com.aname.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aname.api.model.AsociacionDeportiva;
import com.aname.api.repository.IAsociacionDeportivaRepo;
import com.aname.api.service.to.AsociacionDeportivaDTO;

@Service
public class AsociacionDeportivaServiceImpl implements IAsociacionDeportivaService {

	@Autowired
	private IAsociacionDeportivaRepo asociacionDeportivaRepo;

	@Override
	public List<AsociacionDeportivaDTO> listarAsociacionesDeportivas() {

		List<AsociacionDeportiva> lis = this.asociacionDeportivaRepo.buscarTodosAsociacionDeportiva();

		List<AsociacionDeportivaDTO> asociaciones = new ArrayList<AsociacionDeportivaDTO>();

		for (AsociacionDeportiva a : lis) {
			asociaciones.add(this.convertirAsociacionDeportivaService(a));
		}
		return asociaciones;
	}

	@Override
	public AsociacionDeportiva buscarAsociacionDeportiva(Integer id) {

		return this.asociacionDeportivaRepo.buscarAsociacionDeportiva(id);
	}
	
	@Override
	public void actualizarAsociacion(AsociacionDeportiva a) {
		this.asociacionDeportivaRepo.actualizarAsociacionDeportiva(a);
	}

	private AsociacionDeportivaDTO convertirAsociacionDeportivaService(AsociacionDeportiva aso) {
		AsociacionDeportivaDTO a = new AsociacionDeportivaDTO();
		a.setId(aso.getId());
		a.setNombre(aso.getNombre());
		return a;
	}

	private AsociacionDeportiva convertirAsociacionDeportivaServiceTO(AsociacionDeportivaDTO aso) {
		AsociacionDeportiva a = new AsociacionDeportiva();
		a.setId(aso.getId());
		a.setNombre(aso.getNombre());
		return a;
	}

}
