package com.aname.api.service.to.email_DTO;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class EmailFileDTO {

    private String toUser;
    private String subject;
    private String message;
    private MultipartFile file;
    
}