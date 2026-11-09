package com.everywhere.backend;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableCaching
public class EverywhereBackendApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) { 
        SpringApplication.run(EverywhereBackendApplication.class, args);
    }

}