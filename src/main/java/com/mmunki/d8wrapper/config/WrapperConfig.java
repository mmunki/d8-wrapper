package com.mmunki.d8wrapper.config;

import com.mmunki.d8wrapper.model.LogLevel;

/**
 * The configuration of the wrapper.
 *
 * @param logLevel the lowest level of a message that is printed
 * @param failOnWarning {@code true} when a warning of the dexer fails the compilation
 */
public record WrapperConfig(LogLevel logLevel, boolean failOnWarning) {

  /** The configuration that is used when nothing else is configured. */
  public static final WrapperConfig DEFAULT = new WrapperConfig(LogLevel.DEFAULT, false);

  /**
   * Creates the configuration.
   *
   * @param logLevel the lowest level of a message that is printed
   * @param failOnWarning {@code true} when a warning fails the compilation
   */
  public WrapperConfig {
    if (logLevel == null) {
      logLevel = LogLevel.DEFAULT;
    }
  }
}
