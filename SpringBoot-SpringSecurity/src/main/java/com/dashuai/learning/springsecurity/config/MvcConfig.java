package com.dashuai.learning.springsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Mvc config
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/403").setViewName("error/403");
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/user").setViewName("user");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }

}
