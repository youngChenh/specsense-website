package com.specsense;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.specsense.mapper")
public class SpecsenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpecsenseApplication.class, args);
    }
}
