package com.dashuai.learning.oauth2code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        //此处生产环境可替换为从数据库中读取，可使用JdbcUserDetailsManager
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // 创建两个 qq 用户
//        manager.createUser(User.withUsername("250577914")
//                .password("{bcrypt}" + new BCryptPasswordEncoder().encode("123456")).roles("USER").build());
//        manager.createUser(User.withUsername("666666")
//                .password("{bcrypt}" + new BCryptPasswordEncoder().encode("666666")).roles("ADMIN").build());
        manager.createUser(User.withUsername("250577914")
                .password("123456").roles("USER").build());
        manager.createUser(User.withUsername("666666")
                .password("666666").roles("ADMIN").build());
        return manager;
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Password encoder password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.
//                requestMatchers()
//                // 必须登录过的用户才可以进行 oauth2 的授权码申请
//                .antMatchers("/", "/home", "/login", "/oauth/**")
//                .and()
//                .authorizeRequests()
//                .anyRequest().permitAll()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .and()
//                .httpBasic()
//                .disable()
//                .exceptionHandling()
//                .accessDeniedPage("/login?error=true")
//                .and()
//                .logout()
//                .permitAll()
//                .and()
//                // TODO: put CSRF protection back into this endpoint
//                .csrf()
//                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
//                .disable();
        // @formatter:on
        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/fonts/**").permitAll()  // 允许访问资源
                .antMatchers("/", "/login", "/oauth/**").permitAll() //允许访问这三个路由
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .accessDeniedPage("/login?error=true")
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable();
    }
}
