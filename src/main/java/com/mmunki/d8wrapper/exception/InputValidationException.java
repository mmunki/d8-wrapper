package com.mmunki.d8wrapper.exception;

/** An input of the compilation is missing or cannot be used. */
public class InputValidationException extends WrapperException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   */
  public InputValidationException(String message) {
    super(message);
  }

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   * @param cause the failure that caused this one
   */
  public InputValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
