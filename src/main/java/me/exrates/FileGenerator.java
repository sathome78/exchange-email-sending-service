package me.exrates;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.InputEmailDto;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileGenerator {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<InputEmailDto> emails = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            InputEmailDto inputEmailDto = new InputEmailDto();
            inputEmailDto.setEmail(RandomStringUtils.random(10, true, true) + "@gmail.com");
            inputEmailDto.setPubId(RandomStringUtils.random(10, true, true));
            emails.add(inputEmailDto);
        }

        String emailString = objectMapper.writeValueAsString(emails);
        FileUtils.write(new File("test.txt"), emailString, "UTF-8");
    }
}
