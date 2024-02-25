package com.aname.api.service.email_service;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements IEmailService{

    private final String emailUser = "anamenoreply@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    /**
    * Envía un correo electrónico simple al usuario. Este método se utiliza para enviar un correo electrónico al usuario que está conectado.
    * 
    * @param toUser - el usuario al que se envíe el correo electrónico
    * @param subject - el objeto del correo electrónico que se enviará.
    * @param message - El mensaje del correo electrónico a enviar. Si es nulo el objeto se establece en la cadena vacía
    */
    @Override
    public void sendSimpleEmail(String toUser, String subject, String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailUser);
        mailMessage.setTo(toUser);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    /**
    * Método para enviar un email con archivos adjuntos
    * @param toUser - Email al cual se va a enviar
    * @param subject - Asunto del email
    * @param message - Mensaje que se desea enviar
    * @param file - Archivo que se desea adjuntar
    */
    @Override
    public void sendEmailArchivo(String toUser, String subject, String message, File file) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(emailUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
    * Metodo para enviar email simples con solo texto
    * @param toUser - Email al cual se va a enviar
    * @param subject - Asunto del email
    * @param htmlContent - Mensaje que va a ser enviado
    */
    @Override
    public void sendHtmlEmail(String toUser, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailUser);
            helper.setTo(toUser);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
