package ru.netology.diplomcloud;

import org.springframework.boot.SpringApplication;

public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.from(DiplomCloudApplication::main)
            .with(TestConfig.class)
            .run(args);
    }
}
