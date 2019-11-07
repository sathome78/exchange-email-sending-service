package me.exrates.controller;

import me.exrates.model.Email;
import me.exrates.model.InputEmailDto;
import me.exrates.model.StatusModel;
import me.exrates.service.SendMailService;
import me.exrates.service.ServiceEmailExtractor;
import me.exrates.service.WebSocketClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class RestUploadController {
    private final static String PROCESSING = "PROCESSING";
    private final static String FINISHED = "FINISHED";
    private final static String ERROR = "ERROR";
    private final Logger logger = LoggerFactory.getLogger(RestUploadController.class);
    private final SendMailService sendMailService;
    private final ServiceEmailExtractor emailExtractor;
    private final WebSocketClientService wsClient;
    private final ExecutorService executorService;

    @Autowired
    public RestUploadController(SendMailService sendMailService,
                                ServiceEmailExtractor emailExtractor, WebSocketClientService wsClient) {
        this.sendMailService = sendMailService;
        this.emailExtractor = emailExtractor;
        this.wsClient = wsClient;
        this.executorService = Executors.newCachedThreadPool();
    }


    /**
     * MultipartFile file structure:
     * <p>
     * [
     * {
     * "email": "mail@mail.com",
     * "pub_id": "cfd04fccdabdece15d7a"
     * },
     * {
     * "email": "mail1@mail.com",
     * "pub_id": "96cd03055d2231a64d84"
     * }
     * ]
     */
    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadFileMulti(@RequestParam("template") String template,
                                             @RequestParam("subject") String subject,
                                             @RequestParam("file") MultipartFile uploadFile) {

        logger.info("Multiple file upload, subject {}", subject);
        if (StringUtils.isEmpty(uploadFile) || uploadFile.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.OK);
        }

        if (StringUtils.isEmpty(template)) {
            return new ResponseEntity<>("Please input template!", HttpStatus.OK);
        }

        if (StringUtils.isEmpty(subject)) {
            return new ResponseEntity<>("Please input subject!", HttpStatus.OK);
        }

        executorService.execute(() -> sendEmail(uploadFile, subject, template));

        return new ResponseEntity<>("Successfully uploaded - "
                + uploadFile.getOriginalFilename(), HttpStatus.OK);
    }

    private void sendEmail(MultipartFile uploadFile, String subject, String template) {
        List<String> emails = emailExtractor.extractV2(uploadFile);
        for (int i = 0; i < emails.size(); i++) {
            Email email = new Email();
            String toEmail = emails.get(i);
            email.setTo(toEmail);
            email.setSubject(subject);
            email.setMessage(template);
            Properties properties = new Properties();
            properties.setProperty("public_id", Base64.getEncoder().encodeToString(toEmail.getBytes()));
            email.setProperties(properties);
            boolean result = sendMailService.sendMail(email);
            if (result) {
                StatusModel statusModel = new StatusModel(i + 1,
                        emails.size(),
                        toEmail,
                        null,
                        PROCESSING);
                wsClient.sendStatusOk(statusModel);
            } else {
                StatusModel statusModel = new StatusModel(i + 1,
                        emails.size(),
                        toEmail,
                        email.getTo(),
                        ERROR);
                wsClient.sendStatusOk(statusModel);
            }
        }
        StatusModel statusModel = new StatusModel(0,
                emails.size(),
                null,
                null,
                FINISHED);
        wsClient.sendStatusOk(statusModel);
    }
}
