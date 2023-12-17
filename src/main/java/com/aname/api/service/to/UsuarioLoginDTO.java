package com.aname.api.service.to;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

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

    // Getters and setters
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
