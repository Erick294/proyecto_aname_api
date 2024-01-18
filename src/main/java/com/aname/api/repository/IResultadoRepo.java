package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Resultado;

public interface IResultadoRepo {
    
    void insertarResultado(Resultado resultado);

	Resultado buscarResultado(Integer id);

	List<Resultado> buscarTodosResultado();

	void actualizarResultado(Resultado resultado);

	void eliminarResultado(Integer id);
}
