package ru.stroy1click.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class Stroy1ClickOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Stroy1ClickOrderServiceApplication.class, args);
    }

}