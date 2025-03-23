package com.example.application;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ImageGenerationService {
    private static final RestTemplate restTemplate = createRestTemplate();

    private static final String API_URL = "https://api.vyro.ai/v2/image/generations";
    private static final String API_KEY = "API_KEY";
    private static final String STORAGE_API_URI = "STORAGE_API_URI"; //Custom Rest API

    public byte[] generateImageResource(String prompt, String style, String size) {
        if (prompt == null || prompt.isEmpty()) {
            return new byte[]{};
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(API_KEY);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("prompt", prompt);
        requestBody.add("style", style);
        requestBody.add("style_id", 1);
        requestBody.add("aspect_ratio", size);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Fehler beim Abrufen des Bildes: " + response.getStatusCode());
        }
    }

    public int generateStyleID(String style) {
        return switch (style) {
            case "realistic" -> 1;
            case "anime" -> 2;
            case "flux-dev" -> 4;
            default -> 1;
        };
    }

    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(60000);
        return new RestTemplate(factory);
    }

    public void saveImage(byte[] imageSrc, String prompt) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageSrc);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        ImageIO.write(bufferedImage, "png", new File("C:\\Users\\a.gottschalk\\Pictures" + prompt.replace(" ", "") + ".png"));
    }

    public static String postToRest(String imageSrc, String time) {
        if (imageSrc == null || imageSrc.isEmpty()) {
            return "Invalid";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String map = "{\"name\":\""+ time +"\", \"imageSrc\":\""+ imageSrc +"\"}";

        HttpEntity<String> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(STORAGE_API_URI + "/images", request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            return response.getBody();
        } else {
            throw new RuntimeException("Fehler beim Speichern des Bildes: " + response.getStatusCode());
        }
    }

    public String[] getAllImages(){
        return restTemplate.getForObject(STORAGE_API_URI + "/images/imageSrc", String[].class);
    }
}
