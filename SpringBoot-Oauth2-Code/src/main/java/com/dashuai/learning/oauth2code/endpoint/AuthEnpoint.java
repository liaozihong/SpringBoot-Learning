package com.dashuai.learning.oauth2code.endpoint;

import com.dashuai.learning.utils.RequestUrlBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Auth enpoint
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
public class AuthEnpoint {
    @Value("${oauth.url}")
    private String oauthUrl;
    @Value("${aiqiyi.client.id}")
    private String aqyClientId;
    @Value("${youku.client.id}")
    private String ykClientId;
    @Value("${aiqiyi.redirect.url}")
    private String aqyRedirectUrl;
    @Value("${youku.redirect.url}")
    private String ykRedirectUrl;

    /**
     * aqy授权
     *
     * @param response the response
     */
    @GetMapping(value = "/aqy/auth")
    public void aqyAuth(HttpServletResponse response) {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", aqyClientId);
        params.put("response_type", "code");
        params.put("redirect_uri", aqyRedirectUrl);
        try {
            response.sendRedirect(RequestUrlBuilder.buildRequestUrl(oauthUrl, params));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * yk授权
     *
     * @param response the response
     */
    @GetMapping(value = "/yk/auth")
    public void ykAuth(HttpServletResponse response) {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", ykClientId);
        params.put("response_type", "code");
        params.put("redirect_uri", ykRedirectUrl);
        try {
            response.sendRedirect(RequestUrlBuilder.buildRequestUrl(oauthUrl, params));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
