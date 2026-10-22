package com.mmunki.d8wrapper.exception;

/**
 * The base class of every exception that the wrapper throws.
 *
 * <p>The exceptions of the wrapper are checked, a caller has to decide what a failure means for it.
 */
public class WrapperException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   */
  public WrapperException(String message) {
    super(message);
  }

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   * @param cause the failure that caused this one
   */
  public WrapperException(String message, Throwable cause) {
    super(message, cause);
  }
}
