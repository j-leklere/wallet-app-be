package com.walletapp.shared.exception;

public class InvalidPasswordException extends RuntimeException {

  public InvalidPasswordException(String message) {
    super(message);
  }
}
