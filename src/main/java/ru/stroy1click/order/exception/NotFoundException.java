package ru.stroy1click.order.exception;

public class NotFoundException extends RuntimeException {

  private final static String MESSAGE_ID = "Пользователь с %d id не найден";

  private final static String MESSAGE_EMAIL = "Пользователь с электронной почтой %s не найден";

  public NotFoundException(Long id) {
    super(MESSAGE_ID.formatted(id));
  }

  public NotFoundException(String email) {
    super(MESSAGE_EMAIL.formatted(email));
  }
}
