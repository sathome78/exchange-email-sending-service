package me.exrates.service;

import me.exrates.model.Email;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SendMailService {

    private static final Logger logger = LogManager.getLogger(SendMailService.class);
    private final static ExecutorService supportMailExecutors = Executors.newFixedThreadPool(3);
    private final String SUPPORT_EMAIL = "no-reply@exrates.me";
    private final String HOST = "https://exrates.me";

    @Qualifier("SupportMailSender")
    @Autowired
    private JavaMailSender supportMailSender;

    @Qualifier("SendGridMailSender")
    @Autowired
    private JavaMailSender sendGridMailSender;

    public void sendMail(Email email) {
        JavaMailSender sender = defineEmailSender(email.getTo());
        email.setFrom(SUPPORT_EMAIL);
        supportMailExecutors.execute(() -> {
            try {
                sendMail(email, SUPPORT_EMAIL, sender);
            } catch (Exception e) {
                logger.error(e);
                sendMail(email, SUPPORT_EMAIL, sendGridMailSender);
            }
        });
    }

    private void sendMail(Email email, String fromAddress, JavaMailSender mailSender) {
        email.setFrom(fromAddress);
        try {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(email.getFrom());
                message.setTo(email.getTo());
                message.setSubject(email.getSubject());
                message.setText(prepareTemplate(email.getMessage(), email.getProperties().getProperty("public_id")), true);
                if (email.getAttachments() != null) {
                    for (Email.Attachment attachment : email.getAttachments())
                        message.addAttachment(attachment.getName(), attachment.getResource(), attachment.getContentType());
                }
            });
            logger.info("Email success sent to: {}", email.getTo());
        } catch (Exception e) {
            logger.error("Could not send email {}. Reason: {}", email, e.getMessage());
        }
    }

    private JavaMailSender defineEmailSender(String to) {
        String host = to.split("@")[1];

        if (host.equalsIgnoreCase("icloud.com") || host.equalsIgnoreCase("exrates.me")) {
            return sendGridMailSender;
        }
        return supportMailSender;
    }

    private String prepareTemplate(String body, String pubId) {
        body = body
                .replace("{::publicId::}", pubId)
                .replace("{::host::}", HOST);

        return body;
    }
}
