package tw.mmunki.d8wrapper.model;

import java.time.Duration;
import java.util.List;

/**
 * The result of a compilation.
 *
 * <p>The report is a sealed interface, a compilation either succeeds or fails and there is no third
 * case. The pattern matching in the caller decides what happens with the result.
 */
public sealed interface CompilationReport permits SuccessReport, FailureReport {

  /**
   * Returns the DEX files that the compilation produced.
   *
   * @return the artifacts of the compilation, empty when the compilation failed
   */
  List<DexArtifact> artifacts();

  /**
   * Returns the messages that the dexer and the wrapper produced.
   *
   * @return the diagnostics of the compilation
   */
  List<String> diagnostics();

  /**
   * Returns how long the compilation took.
   *
   * @return the duration of the compilation
   */
  Duration duration();

  /**
   * Tells whether the compilation succeeded.
   *
   * @return {@code true} when the DEX files were written
   */
  boolean successful();
}
