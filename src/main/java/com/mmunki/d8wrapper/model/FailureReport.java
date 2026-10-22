package com.mmunki.d8wrapper.model;

import java.time.Duration;
import java.util.List;

/**
 * A compilation that failed.
 *
 * @param reason the reason of the failure, it is meant for the user
 * @param diagnostics the messages that the dexer and the wrapper produced
 * @param duration how long the compilation took before it failed
 */
public record FailureReport(String reason, List<String> diagnostics, Duration duration)
    implements CompilationReport {

  /**
   * Creates the report and makes the list of messages immutable.
   *
   * @param reason the reason of the failure
   * @param diagnostics the messages of the compilation
   * @param duration how long the compilation took
   */
  public FailureReport {
    diagnostics = List.copyOf(diagnostics);
  }

  @Override
  public List<DexArtifact> artifacts() {
    return List.of();
  }

  @Override
  public boolean successful() {
    return false;
  }
}
