package ru.stroy1click.order.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import ru.stroy1click.order.client.NotificationClient;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.exception.ServerErrorResponseException;
import ru.stroy1click.order.exception.ServiceUnavailableException;

@Slf4j
@Service
@CircuitBreaker(name = "productClient")
public class NotificationClientImpl implements NotificationClient {

    private final RestClient restClient;


    public NotificationClientImpl(@Value(value = "${url.service.notification}") String url) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Override
    @Async("asyncTaskExecutor")
    public void sendOrderNotification(OrderDto orderDto) {
        log.info("sendOrderNotification {}", orderDto);
        try {
            this.restClient.post()
                    .body(orderDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new ServerErrorResponseException();
                    })
                    .body(Void.class);
        } catch (ResourceAccessException e) {
            log.error("sendOrderNotification error ", e);
            throw new ServiceUnavailableException();
        }
    }
}
