package com.dashuai.learning.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@SpringBootApplication
@RestController
public class DockerApplication {

    @RequestMapping("/")
    public String getDate(){
        return new Date().toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(DockerApplication.class, args);
    }

}

