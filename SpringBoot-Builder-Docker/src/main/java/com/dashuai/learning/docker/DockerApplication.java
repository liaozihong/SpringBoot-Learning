package com.dashuai.learning.docker;

import com.dashuai.learning.autostarter.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@SpringBootApplication
@RestController
public class DockerApplication {

    @Autowired
    ExampleService exampleService;
    @RequestMapping("/")
    public String getDate(){
        return new Date().toString();
    }

    @RequestMapping("/auto/{name}")
    public String getWap(@PathVariable String name){
        return exampleService.wrap(name);
    }

    public static void main(String[] args) {
        SpringApplication.run(DockerApplication.class, args);
    }

}

