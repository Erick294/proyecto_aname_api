package com.aname.api.service.to;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class UsuarioLoginDTO extends RepresentationModel<UsuarioLoginDTO> implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
    private String password;

    public UsuarioLoginDTO() {}

    public UsuarioLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    
}
