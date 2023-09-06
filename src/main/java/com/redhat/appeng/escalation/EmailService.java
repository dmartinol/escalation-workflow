package com.redhat.appeng.escalation;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EmailService {
    private final static Logger logger = LoggerFactory.getLogger(EmailService.class);

    private Properties prop = new Properties();

    @ConfigProperty(name = "smtp.host")
    private String smtpHost;
    @ConfigProperty(name = "smtp.port")
    private int smtpPort;
    @ConfigProperty(name = "smtp.username")
    private String smtpUsername;
    @ConfigProperty(name = "smtp.password")
    private String smtpPassword;

    @PostConstruct
    private void initMailService() {
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", smtpHost);
        prop.put("mail.smtp.port", smtpPort);
    }

    public void sendEmail(String ticketId, String email) {
        logger.info("Sending email for ticket {} to {}", ticketId, email);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("test@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Mail Subject");

            String msg = String.format("Hey, you must review ticket %s", ticketId);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
