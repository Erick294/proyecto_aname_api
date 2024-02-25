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

	/**
	* Busca todas las asociaciones deportivas de la base de datos
	* @return Lista de asociaciones deportivas
	*/
	@Override
	public List<AsociacionDeportivaDTO> listarAsociacionesDeportivas() {

		List<AsociacionDeportiva> lis = this.asociacionDeportivaRepo.buscarTodosAsociacionDeportiva();

		List<AsociacionDeportivaDTO> asociaciones = new ArrayList<AsociacionDeportivaDTO>();

		for (AsociacionDeportiva a : lis) {
			asociaciones.add(this.convertirAsociacionDeportivaService(a));
		}
		return asociaciones;
	}

	/**
	* Busca una asociación dportiva por su id
	* @param id - Id de la asociacion deportiva
	* @return Objeto AosciacionDpeortiva encontrada
	*/
	@Override
	public AsociacionDeportiva buscarAsociacionDeportiva(Integer id) {

		return this.asociacionDeportivaRepo.buscarAsociacionDeportiva(id);
	}
	
	/**
	* Actualizar una asociación deportiva en la base de datos.
	* @param a - La asociacion deportiva a actualizar
	*/
	@Override
	public void actualizarAsociacion(AsociacionDeportiva a) {
		this.asociacionDeportivaRepo.actualizarAsociacionDeportiva(a);
	}

	/**
	* Convierte una Asociación Deportiva en formato DTO
	* @param aso - Objeto AsociacionDeportiva a transformar
	* @return Objeto con formato DTO con los datos de la estructura correspondiente
	*/
	private AsociacionDeportivaDTO convertirAsociacionDeportivaService(AsociacionDeportiva aso) {
		AsociacionDeportivaDTO a = new AsociacionDeportivaDTO();
		a.setId(aso.getId());
		a.setNombre(aso.getNombre());
		return a;
	}

	/**
	* Convierte un objeto AsociaciónDeportivaDTO un objeto AsociaciónDeportiva
	* @param aso - objeto DTO a ser transformado
	* @return objeto AsociacionDeportiva transformado
	*/
	private AsociacionDeportiva convertirAsociacionDeportivaServiceTO(AsociacionDeportivaDTO aso) {
		AsociacionDeportiva a = new AsociacionDeportiva();
		a.setId(aso.getId());
		a.setNombre(aso.getNombre());
		return a;
	}

}
