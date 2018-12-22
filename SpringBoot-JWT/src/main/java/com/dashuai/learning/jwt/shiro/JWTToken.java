package com.dashuai.learning.jwt.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Jwt token
 * <p/>
 * Created in 2018.12.21
 * <p/>
 *
 * @author Liaozihong
 */
public class JWTToken implements AuthenticationToken {
    private String token;

    /**
     * Instantiates a new Jwt token.
     *
     * @param token the token
     */
    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}
