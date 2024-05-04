package com.ayed.booknetwork.exceptions;

public class OperationNotPermittedException extends RuntimeException {
  public OperationNotPermittedException() {}

  public OperationNotPermittedException(String message) {
    super(message);
  }
}
