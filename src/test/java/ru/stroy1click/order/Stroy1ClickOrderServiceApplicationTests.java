package ru.stroy1click.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Stroy1ClickOrderServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
