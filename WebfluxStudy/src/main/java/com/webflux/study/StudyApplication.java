package com.webflux.study;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.webflux.study.common.Constants.UPLOAD_ROOT;

@SpringBootApplication
public class StudyApplication implements WebFluxConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class, args);
    }

    @Bean
    public CommandLineRunner setUp() {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
        };
    }


}
