package com.aname.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aname.api.model.Usuario;

public interface IUsuarioRepo extends JpaRepository<Usuario, Long>{

	Usuario findByEmail(String email);
	
}
