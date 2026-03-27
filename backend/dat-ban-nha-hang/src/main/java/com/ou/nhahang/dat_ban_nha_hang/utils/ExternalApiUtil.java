package com.ou.nhahang.dat_ban_nha_hang.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;

@Component
public class ExternalApiUtil {

    @Autowired
    private RestTemplate restTemplate;

    public <T> T sendGetRequest(String baseUrl, Map<String, Object> params, Class<T> responseType) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
            params.forEach((key, value) -> builder.queryParam(key, value));

            String finalUrl = builder.toUriString();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API: " + e.getMessage());
        }
    }

    public <T, R> R sendPostRequest(String baseUrl, T requestBody, Class<R> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<R> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API: " + e.getMessage());
        }
    }

    public <T, R> R sendPutRequest(String baseUrl, T requestBody, Class<R> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<R> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API: " + e.getMessage());
        }
    }

    public <T> T sendDeleteRequest(String baseUrl, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(baseUrl, HttpMethod.DELETE, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi gọi API: " + e.getMessage());
        }
    }
}
