package ru.stroy1click.order;

import org.springframework.boot.SpringApplication;

public class TestStroy1ClickOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(Stroy1ClickOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
