package com.aname.api.service.to.email_DTO;

import lombok.Data;

@Data
public class EmailDTO {

    private String toUser;
    private String subject;
    private String message;
    
}
