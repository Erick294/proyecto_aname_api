package com.aname.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.aname.api.model.Campeonato;

public interface ICampeonatoRepo {

    void insertarCampeonato(Campeonato campeonato);

    Campeonato buscarCampeonato(Integer id);

    List<Campeonato> buscarTodosCampeonato();

    void actualizarCampeonato(Campeonato campeonato);

    void eliminarCampeonato(Integer id);

    List<Campeonato> buscarCampeonatosDisponibles();

    @Query(value = "SELECT usua.usua_nombres, usua.usua_apellidos, cate.cate_nombre, cate.cate_genero, asde.asde_nombre, camp.camp_nombre "
            +
            "FROM usuarios usua " +
            "INNER JOIN competidores comp ON comp.usua_id = usua.usua_id " +
            "INNER JOIN campeonatos_competidores caco ON caco.comp_id = comp.comp_id " +
            "INNER JOIN campeonatos camp ON camp.camp_id = caco.camp_id " +
            "INNER JOIN asociaciones_deportivas asde ON asde.asde_id = comp.asde_id " +
            "INNER JOIN categorias cate ON (EXTRACT(year FROM AGE(usua.usua_fecha_nacimiento)) BETWEEN cate.cate_edad_minima AND cate.cate_edad_maxima) AND cate.cate_genero = usua.usua_sexo "
            +
            "WHERE camp.camp_id =  1 " +
            "ORDER BY cate.cate_nombre DESC", nativeQuery = true)
    List<Object[]> findUsuariosWithCampeonatos();

    
    @Query(value = "SELECT camp.camp_nombre, prue.prue_nombre, usua.usua_nombres, usua.usua_apellidos, cate.cate_nombre, asde.asde_nombre, cate.cate_genero " +
    "FROM pruebas prue " +
    "INNER JOIN campeonatos_pruebas capr ON capr.prue_id = prue.prue_id " +
    "INNER JOIN campeonatos camp ON capr.camp_id = camp.camp_id " +
    "INNER JOIN campeonatos_competidores caco ON caco.camp_id = camp.camp_id " +
    "INNER JOIN competidores comp ON caco.comp_id = comp.comp_id " +
    "INNER JOIN usuarios usua ON comp.usua_id = usua.usua_id " +
    "INNER JOIN competidores_pruebas copr ON  copr.comp_id = comp.comp_id AND copr.prue_id = prue.prue_id " +
    "INNER JOIN asociaciones_deportivas asde ON asde.asde_id = comp.asde_id " +
    "INNER JOIN categorias cate ON (EXTRACT(year FROM AGE(usua.usua_fecha_nacimiento)) BETWEEN cate.cate_edad_minima AND cate.cate_edad_maxima) AND cate.cate_genero = usua.usua_sexo " +
    "WHERE camp.camp_id =  1 " +
    "ORDER BY cate.cate_nombre DESC", nativeQuery = true)
List<Object[]> findPruebasCampeonatos();

}
