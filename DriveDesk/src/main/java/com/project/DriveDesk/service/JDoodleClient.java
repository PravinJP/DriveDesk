package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.JDoodleRequestDTO;
import com.project.DriveDesk.DTO.JDoodleResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JDoodleClient {

    @Value("${jdoodle.clientId}")
    private String clientId;

    @Value("${jdoodle.clientSecret}")
    private String clientSecret;

    private final String JDoodle_URL = "https://api.jdoodle.com/v1/execute";

    public JDoodleResponseDTO execute(String code, String input, String language, String versionIndex) {
        JDoodleRequestDTO request = new JDoodleRequestDTO();
        request.setClientId(clientId);
        request.setClientSecret(clientSecret);
        request.setScript(code);
        request.setStdin(input);
        request.setLanguage(language);
        request.setVersionIndex(versionIndex);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(JDoodle_URL, request, JDoodleResponseDTO.class);
    }
}
