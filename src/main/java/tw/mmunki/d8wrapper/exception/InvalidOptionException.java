package tw.mmunki.d8wrapper.exception;

/** The command line holds an option that the wrapper does not know. */
public class InvalidOptionException extends WrapperException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates the exception.
   *
   * @param message the message that explains the failure
   */
  public InvalidOptionException(String message) {
    super(message);
  }
}
