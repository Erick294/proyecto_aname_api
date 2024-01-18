package com.aname.api.service.to.email_DTO;

import org.springframework.web.multipart.MultipartFile;

public class EmailFileDTO {

    private String toUser;
    private String subject;
    private String message;
    private MultipartFile file;

    public EmailFileDTO() {
    }
    
    public EmailFileDTO(String toUser, String subject, String message, MultipartFile file) {
        this.toUser = toUser;
        this.subject = subject;
        this.message = message;
        this.file = file;
    }
    public String getToUser() {
        return toUser;
    }
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public MultipartFile getFile() {
        return file;
    }
    public void setFile(MultipartFile file) {
        this.file = file;
    }

    
    
}