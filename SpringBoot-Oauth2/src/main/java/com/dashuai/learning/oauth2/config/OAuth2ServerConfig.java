package com.dashuai.learning.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
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
    private static final String DEMO_RESOURCE_ID = "order";

    /**
     * Resource server configuration
     * <p/>
     * Created in 2019.05.14
     * <p/>
     *
     * @author Liaozihong
     */
    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(DEMO_RESOURCE_ID).stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            //配置order访问控制，必须认证过后才可以访问
            http.authorizeRequests().antMatchers("/order/**")
                    .authenticated();
            http.authorizeRequests()
                    .antMatchers("/order/**").access("#oauth2.hasScope('app')")
                    .antMatchers("/product/**").access("#oauth2.hasScope('web')");
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

        /**
         * The Authentication manager.
         */
        @Autowired
        AuthenticationManager authenticationManager;
        /**
         * The Redis connection factory.
         */
        @Autowired
        RedisConnectionFactory redisConnectionFactory;

        @Autowired
        UserDetailsService userDetailsService;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

//        password 方案一：明文存储，用于测试，不能用于生产
//        String finalSecret = "123456";
//        password 方案二：用 BCrypt 对密码编码
//        String finalSecret = new BCryptPasswordEncoder().encode("123456");
            // password 方案三：支持多种编码，通过密码的前缀区分编码方式
            String finalSecret = "{bcrypt}" + new BCryptPasswordEncoder().encode("123456");
            //配置两个客户端,一个用于password认证一个用于client认证
            //并且设置两个作用域web、app，分别针对不同场景
            clients.inMemory().withClient("client_1")
                    .resourceIds(DEMO_RESOURCE_ID)
                    .authorizedGrantTypes("client_credentials", "refresh_token")
                    .scopes("web")
                    .authorities("oauth2")
                    .secret(finalSecret)
                    .and().withClient("client_2")
                    .resourceIds(DEMO_RESOURCE_ID)
                    .authorizedGrantTypes("password", "refresh_token")
                    .scopes("app")
                    .authorities("oauth2")
                    .secret(finalSecret);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            //refresh_token,需注入UserDetailsService
            endpoints
                    .tokenStore(new RedisTokenStore(redisConnectionFactory))
                    .authenticationManager(authenticationManager)
                    .userDetailsService(userDetailsService)
                    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
            endpoints.reuseRefreshTokens(true);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            //允许表单认证
            oauthServer.allowFormAuthenticationForClients();
        }

    }
}
