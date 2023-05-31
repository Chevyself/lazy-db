package me.googas.jsongo.exception;


public class TestSetupException extends RuntimeException {
  public TestSetupException(String message, Throwable cause) {
    super(message, cause);
  }

  public TestSetupException(Throwable cause) {
    super(cause);
  }
}
