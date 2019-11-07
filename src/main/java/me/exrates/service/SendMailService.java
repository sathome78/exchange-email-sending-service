package me.exrates.service;

import me.exrates.model.Email;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;

@Service
public class SendMailService {

    private static final Logger logger = LogManager.getLogger(SendMailService.class);
    private final static String SUPPORT_EMAIL = "no-reply@exrates.top";
    private static int SWITCH = 1;
    @Qualifier("SupportMailSender")
    @Autowired
    private JavaMailSender supportMailSender;

    @Qualifier("SendGridMailSender")
    @Autowired
    private JavaMailSender sendGridMailSender;

    @Qualifier("SupportMailSenderV2")
    @Autowired
    private JavaMailSender sendGridMailSenderV2;

    public boolean sendMail(Email email) {
        JavaMailSender sender = defineEmailSender(email.getTo());
        email.setFrom(SUPPORT_EMAIL);
        try {
            sendMail(email, SUPPORT_EMAIL, sender);
        } catch (Exception e) {
            logger.error("Could not send email {}, sender {}, Reason: {}",
                    email,
                    ((JavaMailSenderImpl) sender).getJavaMailProperties().getProperty("name"),
                    e.getMessage());
            logger.error(e);
            return false;
        }
        return true;
    }

    private void sendMail(Email email, String fromAddress, JavaMailSender mailSender) {
        email.setFrom(fromAddress);
        mailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(new InternetAddress(email.getFrom(), "Exrates"));
            message.setTo(email.getTo());
            message.setSubject(email.getSubject());
            message.setText(prepareTemplate(email.getMessage(), email.getProperties().getProperty("public_id")), true);
            if (email.getAttachments() != null) {
                for (Email.Attachment attachment : email.getAttachments())
                    message.addAttachment(attachment.getName(), attachment.getResource(), attachment.getContentType());
            }
        });
        logger.info("Email success sent to: {}, sender {}", email.getTo(),
                ((JavaMailSenderImpl) mailSender).getJavaMailProperties().getProperty("name"));
    }

    private JavaMailSender defineEmailSender(String to) {
        String host = to.split("@")[1];

        if (host.equalsIgnoreCase("icloud.com") || host.equalsIgnoreCase("exrates.me")) {
            return sendGridMailSender;
        }

        if (SWITCH == 1) {
            SWITCH = 2;
            return supportMailSender;
        } else {
            SWITCH = 1;
            return sendGridMailSenderV2;
        }
    }

    private String prepareTemplate(String body, String pubId) {
        body = body.replace("{::token::}", pubId);
        return body;
    }
}
