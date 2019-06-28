/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dashuai.learning.oauth2code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * O auth 2 server config
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class OAuth2ServerConfig {

    private static final String QQ_RESOURCE_ID = "qq";

    /**
     * Resource server configuration
     * <p/>
     * Created in 2019.05.14
     * <p/>
     *
     * @author Liaozihong
     */
    @Configuration
    @EnableResourceServer()
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(QQ_RESOURCE_ID).stateless(true);
            // 如果关闭 stateless，则 accessToken 使用时的 session id 会被记录，后续请求不携带 accessToken 也可以正常响应
//            resources.resourceId(QQ_RESOURCE_ID).stateless(false);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .requestMatchers()
                    // 保险起见，防止被主过滤器链路拦截
                    .antMatchers("/qq/**").and()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/qq/info/**").access("#oauth2.hasScope('get_user_info')")
                    .antMatchers("/qq/fans/**").access("#oauth2.hasScope('get_fanslist')");
            // @formatter:on
        }

    }

    /**
     * Authorization server configuration
     * <p/>
     * Created in 2019.05.14
     * <p/>
     *
     * @author Liaozihong
     */
    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;
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

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

            // @formatter:off
            clients.inMemory().withClient(aqyClientId)
                    .resourceIds(QQ_RESOURCE_ID)
                    .authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT")
                    .scopes("get_user_info", "get_fanslist")
                    .secret(aqyClientSecret)
                    .redirectUris(aqyRedirectUrl)
//                    .autoApprove(true) //设置不自动授权
                    //如果有自定义scope，并且有自定义授权页面，则需要在授权页面给对应scope授权或这里设置自动授权scope,会获取code失败
//                    .autoApprove("get_user_info", "get_fanslist")
                    .and()
                    .withClient(ykClientId)
                    .resourceIds(QQ_RESOURCE_ID)
                    .authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT")
                    .scopes("get_user_info", "get_fanslist")
                    .autoApprove("get_user_info", "get_fanslist")
                    .secret(ykClientSecret)
                    .redirectUris(ykRedirectUrl);
            // @formatter:on
        }

        /**
         * Approval store approval store.
         *
         * @return the approval store
         */
        @Bean
        public ApprovalStore approvalStore() {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore());
            return store;
        }

        /**
         * The Redis connection factory.
         */
        @Autowired
        RedisConnectionFactory redisConnectionFactory;

        /**
         * Token store token store.
         *
         * @return the token store
         */
        @Bean
        public TokenStore tokenStore() {
            //暂时加载到内存
//            return new InMemoryTokenStore();
            // 需要使用 redis 的话，放开这里
            return new RedisTokenStore(redisConnectionFactory);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints.tokenStore(tokenStore())
                    .authenticationManager(authenticationManager)
                    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                    .pathMapping("/oauth/confirm_access", "/custom/confirm_access");
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            oauthServer.realm(QQ_RESOURCE_ID).allowFormAuthenticationForClients();
        }

    }


}
