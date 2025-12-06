package ru.stroy1click.order.integration;

import org.springframework.boot.SpringApplication;
import ru.stroy1click.order.Stroy1ClickOrderServiceApplication;

public class TestStroy1ClickOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(Stroy1ClickOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
