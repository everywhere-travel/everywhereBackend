package com.everywhere.backend;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication 
public class EverywhereBackendApplication {

    public static void main(String[] args) { 
        SpringApplication.run(EverywhereBackendApplication.class, args);
    }

}