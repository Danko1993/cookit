package com.example.cookit.services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationLink(String to, String activationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("cookit93@gmail.com");
        helper.setTo(to);
        helper.setSubject("Account Activation");
        helper.setText(activationLink + "< < Click here to activate Your account");
        mailSender.send(message);
    }
}
