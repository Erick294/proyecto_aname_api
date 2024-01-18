package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.AsociacionDeportiva;

public interface IAsociacionDeportivaRepo {

    void insertarAsociacionDeportiva(AsociacionDeportiva asociacionDeportiva);

	AsociacionDeportiva buscarAsociacionDeportiva(Integer id);

	List<AsociacionDeportiva> buscarTodosAsociacionDeportiva();

	void actualizarAsociacionDeportiva(AsociacionDeportiva asociacionDeportiva);

	void eliminarAsociacionDeportiva(Integer id);
    
}
