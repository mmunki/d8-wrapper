package tw.mmunki.d8wrapper.cli;

/** The exit codes of the wrapper. */
public enum ExitCode {
  /** The compilation succeeded. */
  SUCCESS(0),
  /** The command line could not be understood. */
  USAGE_ERROR(2),
  /** The request was understood but cannot succeed. */
  VALIDATION_ERROR(3),
  /** The dexer refused to compile the inputs. */
  COMPILATION_FAILED(4),
  /** The wrapper hit a failure it does not know about. */
  INTERNAL_ERROR(70);

  private final int status;

  ExitCode(int status) {
    this.status = status;
  }

  /**
   * Returns the status of the exit code.
   *
   * @return the status that is handed to the operating system
   */
  public int status() {
    return status;
  }
}
