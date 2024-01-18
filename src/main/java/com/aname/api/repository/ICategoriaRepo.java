package com.aname.api.repository;

import java.util.List;

import com.aname.api.model.Categoria;

public interface ICategoriaRepo {

    void insertarCategoria(Categoria categoria);

	Categoria buscarCategoria(Integer id);

	List<Categoria> buscarTodosCategoria();

	void actualizarCategoria(Categoria categoria);

	void eliminarCategoria(Integer id);
}
