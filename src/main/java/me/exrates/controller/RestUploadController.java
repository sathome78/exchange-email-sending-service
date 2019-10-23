package me.exrates.controller;

import me.exrates.service.ServiceEmailExtractor;
import me.exrates.model.Email;
import me.exrates.model.InputEmailDto;
import me.exrates.service.SendMailService;
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

import java.util.List;
import java.util.Properties;

@RestController
public class RestUploadController {

    private final Logger logger = LoggerFactory.getLogger(RestUploadController.class);

    private final SendMailService sendMailService;
    private final ServiceEmailExtractor emailExtractor;

    @Autowired
    public RestUploadController(SendMailService sendMailService,
                                ServiceEmailExtractor emailExtractor) {
        this.sendMailService = sendMailService;
        this.emailExtractor = emailExtractor;
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
        if (StringUtils.isEmpty(uploadFile)) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.OK);
        }

        if (StringUtils.isEmpty(template)) {
            return new ResponseEntity<>("Please input template!", HttpStatus.OK);
        }

        if (StringUtils.isEmpty(subject)) {
            return new ResponseEntity<>("Please input subject!", HttpStatus.OK);
        }

        List<InputEmailDto> emails = emailExtractor.extract(uploadFile);

        for (InputEmailDto inputModel : emails) {
            Email email = new Email();
            email.setTo(inputModel.getEmail());
            email.setSubject(subject);
            email.setMessage(template);
            Properties properties = new Properties();
            properties.setProperty("public_id", inputModel.getPubId());
            email.setProperties(properties);
            sendMailService.sendMail(email);
        }
        return new ResponseEntity<>("Successfully uploaded - "
                + uploadFile.getOriginalFilename(), HttpStatus.OK);
    }
}
