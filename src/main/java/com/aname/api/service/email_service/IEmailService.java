package com.aname.api.service.email_service;

import java.io.File;

public interface IEmailService {
    
    void sendSimpleEmail(String toUser, String subject, String message);

    void sendEmailArchivo(String toUser, String subject, String message, File file);
}
