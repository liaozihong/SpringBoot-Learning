package com.dashaui.learning.multisource.config;

import com.dashaui.learning.multisource.service.PeopleService;
import com.dashaui.learning.multisource.service.UserService;
import com.dashaui.learning.multisource.service.impl.PeopleServiceImpl;
import com.dashaui.learning.multisource.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class SpringConfiguration {
    /**
     * User service user service.
     *
     * @return the user service
     */
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    /**
     * People service people service.
     *
     * @return the people service
     */
    @Bean
    public PeopleService peopleService() {
        return new PeopleServiceImpl();
    }
}

