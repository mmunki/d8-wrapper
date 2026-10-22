package com.mmunki.d8wrapper.exception;

/** The D8 dexer refused to compile the inputs. */
public class CompilationFailedException extends WrapperException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   */
  public CompilationFailedException(String message) {
    super(message);
  }

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   * @param cause the failure that caused this one
   */
  public CompilationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
