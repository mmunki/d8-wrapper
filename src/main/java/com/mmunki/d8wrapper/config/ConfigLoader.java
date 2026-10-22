package com.mmunki.d8wrapper.config;

/** Reads the configuration of the wrapper. */
public interface ConfigLoader {

  /**
   * Reads the configuration.
   *
   * @return the configuration of the wrapper, never {@code null}
   */
  WrapperConfig load();
}
