package ru.stroy1click.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic orderCreatedTopic(){
        return TopicBuilder.name("order-created-events")
                .replicas(1)
                .partitions(3)
                .build();
    }
}
