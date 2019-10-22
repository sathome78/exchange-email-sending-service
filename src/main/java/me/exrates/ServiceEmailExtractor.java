package me.exrates;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.InputEmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ServiceEmailExtractor {

    private final Logger logger = LoggerFactory.getLogger(ServiceEmailExtractor.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public ServiceEmailExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<InputEmailDto> extract(MultipartFile multipartFile) {
        String inputString = "";
        try {
            inputString = new String(multipartFile.getBytes(), "UTF-8");
        } catch (IOException e) {
            logger.error("Error read file", e);
        }
        List<InputEmailDto> result = null;
        try {
            result = objectMapper.readValue(inputString, new TypeReference<List<InputEmailDto>>() {
            });
        } catch (IOException e) {
            logger.error("Error parse string to InputEmailDto", e);
        }
        return result;
    }

}
