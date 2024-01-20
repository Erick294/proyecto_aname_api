package com.aname.api.service;

import java.util.List;

import com.aname.api.model.AsociacionDeportiva;
import com.aname.api.service.to.AsociacionDeportivaDTO;

public interface IAsociacionDeportivaService {
	
	List<AsociacionDeportivaDTO> listarAsociacionesDeportivas();

	AsociacionDeportiva buscarAsociacionDeportiva(Integer id);
	

}
