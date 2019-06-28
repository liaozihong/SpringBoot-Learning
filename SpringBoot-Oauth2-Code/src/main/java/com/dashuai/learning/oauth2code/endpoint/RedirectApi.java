package com.dashuai.learning.oauth2code.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Redirect api
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@Slf4j
public class RedirectApi {
    /**
     * The Rest template.
     */
    @Autowired
    RestTemplate restTemplate;
    @Value("${project.host}")
    private String projectHost;
    @Value("${aiqiyi.redirect.url}")
    private String aqyRedirectUrl;
    @Value("${youku.redirect.url}")
    private String ykRedirectUrl;
    @Value("${aiqiyi.client.id}")
    private String aqyClientId;
    @Value("${youku.client.id}")
    private String ykClientId;
    @Value("${aiqiyi.client.secret}")
    private String aqyClientSecret;
    @Value("${youku.client.secret}")
    private String ykClientSecret;

    /**
     * Gets token.
     *
     * @param code the code
     * @return the token
     */
    @RequestMapping("/aiqiyi/qq/redirect")
    public String getAiQiYiToken(@RequestParam String code) {
        log.info("receive code {}", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", aqyClientId);
        params.add("client_secret", aqyClientSecret);
        params.add("redirect_uri", aqyRedirectUrl);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(projectHost + "/oauth/token", requestEntity, String.class);
        String token = response.getBody();
        log.info("token => {}", token);
        return token;
    }

    /**
     * Gets token.
     *
     * @param code the code
     * @return the token
     */
    @RequestMapping("/youku/qq/redirect")
    public String getYouKuToken(@RequestParam String code) {
        log.info("receive code {}", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", ykClientId);
        params.add("client_secret", ykClientSecret);
        params.add("redirect_uri", ykRedirectUrl);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(projectHost + "/oauth/token", requestEntity, String.class);
        String token = response.getBody();
        log.info("token => {}", token);
        return token;
    }
}
