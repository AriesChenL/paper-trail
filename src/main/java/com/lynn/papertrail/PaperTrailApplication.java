package com.lynn.papertrail;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lynn
 */
@SpringBootApplication
@MapperScan("com.lynn.papertrail.mapper")
public class PaperTrailApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaperTrailApplication.class, args);
    }

}
