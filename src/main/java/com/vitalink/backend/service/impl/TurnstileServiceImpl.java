package com.vitalink.backend.service.impl;

import com.vitalink.backend.exception.TurnstileVerificationException;
import com.vitalink.backend.service.TurnstileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TurnstileServiceImpl implements TurnstileService {

    private final RestTemplate restTemplate;

    @Value("${cloudflare.turnstile.secret-key}")
    private String secretKey;

    @Value("${cloudflare.turnstile.verify-url}")
    private String verifyUrl;

    public TurnstileServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void verify(String cfToken) {
        if (!StringUtils.hasText(cfToken)) {
            throw new TurnstileVerificationException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", cfToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(verifyUrl, request, Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
            throw new TurnstileVerificationException();
        }
    }
}
