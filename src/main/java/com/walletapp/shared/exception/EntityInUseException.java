package com.walletapp.shared.exception;

public class EntityInUseException extends RuntimeException {

  public EntityInUseException(String message) {
    super(message);
  }
}
