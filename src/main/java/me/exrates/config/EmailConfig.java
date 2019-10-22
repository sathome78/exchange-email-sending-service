package me.exrates.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    
    @Value("${mail.mail_support.host}")
    private String mailSupportHost;
    @Value("${mail.mail_support.port}")
    private String mailSupportPort;
    @Value("${mail.mail_support.protocol}")
    private String mailSupportProtocol;
    @Value("${mail.mail_support.user}")
    private String mailSupportUser;
    @Value("${mail.mail_support.password}")
    private String mailSupportPassword;
    
    @Value("${mail.send_grid.host}")
    private String mailSendGridHost;
    @Value("${mail.send_grid.port}")
    private String mailSendGridPort;
    @Value("${mail.send_grid.protocol}")
    private String mailSendGridProtocol;
    @Value("${mail.send_grid.user}")
    private String mailSendGridUser;
    @Value("${mail.send_grid.password}")
    private String mailSendGridPassword;

    @Bean(name = "SupportMailSender")
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSupportHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSupportPort));
        mailSenderImpl.setProtocol(mailSupportProtocol);
        mailSenderImpl.setUsername(mailSupportUser);
        mailSenderImpl.setPassword(mailSupportPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailSupportHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "SendGridMailSender")
    public JavaMailSenderImpl infoMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSendGridHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSendGridPort));
        mailSenderImpl.setProtocol(mailSendGridProtocol);
        mailSenderImpl.setUsername(mailSendGridUser);
        mailSenderImpl.setPassword(mailSendGridPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", false);
        javaMailProps.put("mail.smtp.ssl.trust", mailSendGridHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }
}
