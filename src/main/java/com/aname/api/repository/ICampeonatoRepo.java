package com.aname.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.aname.api.model.Campeonato;

public interface ICampeonatoRepo{

    void insertarCampeonato(Campeonato campeonato);

    Campeonato buscarCampeonato(Integer id);

    List<Campeonato> buscarTodosCampeonato();

    void actualizarCampeonato(Campeonato campeonato);

    void eliminarCampeonato(Integer id);

    List<Campeonato> buscarCampeonatosDisponibles();

   

}
