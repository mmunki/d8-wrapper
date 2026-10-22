package com.mmunki.d8wrapper.model;

import java.time.Duration;
import java.util.List;

/**
 * A compilation that produced DEX files.
 *
 * @param artifacts the DEX files that were written
 * @param diagnostics the messages that the dexer and the wrapper produced
 * @param duration how long the compilation took
 */
public record SuccessReport(
    List<DexArtifact> artifacts, List<String> diagnostics, Duration duration)
    implements CompilationReport {

  /**
   * Creates the report and makes the collections immutable.
   *
   * @param artifacts the DEX files that were written
   * @param diagnostics the messages of the compilation
   * @param duration how long the compilation took
   */
  public SuccessReport {
    artifacts = List.copyOf(artifacts);
    diagnostics = List.copyOf(diagnostics);
  }

  @Override
  public boolean successful() {
    return true;
  }
}
