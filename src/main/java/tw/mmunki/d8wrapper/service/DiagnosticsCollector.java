package tw.mmunki.d8wrapper.service;

import com.android.tools.r8.Diagnostic;
import com.android.tools.r8.DiagnosticsHandler;
import tw.mmunki.d8wrapper.model.LogLevel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Collects the diagnostics of the D8 dexer.
 *
 * <p>The dexer reports its diagnostics from the threads that do the work, the collector is
 * therefore thread safe. The handler is the bridge between the dexer and the wrapper, it keeps the
 * dexer from writing to the console.
 */
public final class DiagnosticsCollector implements DiagnosticsHandler {

  private final List<String> messages = new CopyOnWriteArrayList<>();
  private final LogLevel level;

  /**
   * Creates the collector.
   *
   * @param level the lowest level of a message that is kept
   */
  public DiagnosticsCollector(LogLevel level) {
    this.level = level;
  }

  @Override
  public void error(Diagnostic diagnostic) {
    add(LogLevel.ERROR, diagnostic);
  }

  @Override
  public void warning(Diagnostic diagnostic) {
    add(LogLevel.WARN, diagnostic);
  }

  @Override
  public void info(Diagnostic diagnostic) {
    add(LogLevel.INFO, diagnostic);
  }

  /**
   * Returns the collected messages.
   *
   * @return the messages of the compilation
   */
  public List<String> messages() {
    return List.copyOf(messages);
  }

  /**
   * Tells whether the dexer reported an error.
   *
   * @return {@code true} when at least one error was reported
   */
  public boolean hasErrors() {
    return messages.stream().anyMatch(message -> message.startsWith(LogLevel.ERROR.label()));
  }

  /**
   * Adds a message of the dexer to the collected ones.
   *
   * @param severity the severity of the message
   * @param diagnostic the diagnostic of the dexer
   */
  private void add(LogLevel severity, Diagnostic diagnostic) {
    if (!level.includes(severity)) {
      return;
    }
    messages.add(severity.label() + ": " + diagnostic.getDiagnosticMessage());
  }
}
