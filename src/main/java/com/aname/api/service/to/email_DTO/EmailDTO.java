package com.aname.api.service.to.email_DTO;

public class EmailDTO {

    private String toUser;
    private String subject;
    private String message;

    public EmailDTO() {
    }

    public EmailDTO(String toUser, String subject, String message) {
        this.toUser = toUser;
        this.subject = subject;
        this.message = message;
    }

    @Override
    public String toString() {
        return "EmailDTO [toUser=" + toUser + ", subject=" + subject + ", message=" + message + "]";
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

    
    
}
