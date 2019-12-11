package com.dashuai.learning.sentinel.api;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestApi {
    @RequestMapping("/test")
    @SentinelResource("test")
    public String health() {
        return new Date().toString();
    }

    @GetMapping(value = "/mye")
    @SentinelResource("mye")
    public String mye() {
        if (true) {
            throw new RuntimeException("mye");
        }
        return "mye Sentinel";
    }

    @GetMapping(value = "/myrate")
    @SentinelResource("myrate")
    public String myrate() {
        return "myrate Sentinel";
    }
}