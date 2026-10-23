package tw.mmunki.d8wrapper.model;

/**
 * Severity of a message that the wrapper or the D8 dexer produces.
 *
 * <p>The levels are ordered, a level includes every level below it. {@link #OFF} disables the
 * output completely.
 */
public enum LogLevel {
  OFF("off", 0),
  ERROR("error", 1),
  WARN("warn", 2),
  INFO("info", 3),
  DEBUG("debug", 4);

  /** The level that is used when nothing else is configured. */
  public static final LogLevel DEFAULT = INFO;

  private final String label;
  private final int severity;

  LogLevel(String label, int severity) {
    this.label = label;
    this.severity = severity;
  }

  /**
   * Returns the lower case name of the level, the name that is used on the command line.
   *
   * @return the label of the level
   */
  public String label() {
    return label;
  }

  /**
   * Returns the numeric severity of the level, a higher value means more output.
   *
   * @return the severity of the level
   */
  public int severity() {
    return severity;
  }

  /**
   * Tells whether messages of the given level are shown with this level.
   *
   * @param other the level of a message
   * @return {@code true} when a message of that level is shown
   */
  public boolean includes(LogLevel other) {
    return this != OFF && other.severity <= this.severity;
  }

  /**
   * Resolves a level from its label.
   *
   * @param label the label of the level, case insensitive
   * @return the matching level
   * @throws IllegalArgumentException when there is no level with that label
   */
  public static LogLevel fromLabel(String label) {
    for (LogLevel level : values()) {
      if (level.label.equalsIgnoreCase(label)) {
        return level;
      }
    }
    throw new IllegalArgumentException("unknown log level: " + label);
  }
}
