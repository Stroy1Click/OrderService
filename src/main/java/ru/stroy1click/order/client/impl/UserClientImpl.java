package ru.stroy1click.order.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import ru.stroy1click.order.client.UserClient;
import ru.stroy1click.order.dto.UserDto;
import ru.stroy1click.order.exception.ServiceUnavailableException;
import ru.stroy1click.order.util.ValidationErrorUtils;


@Slf4j
@Service
@CircuitBreaker(name = "userClient")
public class UserClientImpl implements UserClient {

    private final RestClient restClient;

    private final MessageSource messageSource;

    public UserClientImpl(@Value(value = "${url.service.user}") String url, MessageSource messageSource) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
        this.messageSource = messageSource;
    }

    @Override
    public UserDto get(Long id) {
        try {
            return this.restClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,(request, response) -> {
                        ValidationErrorUtils.validateStatus(response);
                    })
                    .body(UserDto.class);
        }  catch (ResourceAccessException e){
            log.error("get error ", e);
            throw new ServiceUnavailableException();
        }

    }
}
