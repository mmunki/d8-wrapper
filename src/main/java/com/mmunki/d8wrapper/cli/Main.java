package com.mmunki.d8wrapper.cli;

import com.mmunki.d8wrapper.config.ServiceRegistry;
import com.mmunki.d8wrapper.config.WrapperConfig;
import com.mmunki.d8wrapper.exception.InputValidationException;
import com.mmunki.d8wrapper.exception.InvalidOptionException;
import com.mmunki.d8wrapper.exception.WrapperException;
import com.mmunki.d8wrapper.model.CompilationReport;
import com.mmunki.d8wrapper.model.DexArtifact;
import com.mmunki.d8wrapper.model.DexRequest;
import com.mmunki.d8wrapper.model.FailureReport;
import com.mmunki.d8wrapper.model.LogLevel;
import com.mmunki.d8wrapper.model.SuccessReport;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;

/**
 * The entry point of the wrapper.
 *
 * <p>The class reads the command line, builds the request, runs the compilation and reports what
 * happened. It returns the exit code of the run, the operating system gets it from {@link
 * #main(String[])}.
 */
public final class Main {

  private Main() {}

  /**
   * Runs the wrapper and ends the process with the exit code of the run.
   *
   * @param args the arguments of the command line
   */
  public static void main(String[] args) {
    int status = run(args, System.out, System.err);
    System.exit(status);
  }

  /**
   * Runs the wrapper.
   *
   * @param args the arguments of the command line
   * @param out the stream the result is printed to
   * @param err the stream the failures are printed to
   * @return the exit code of the run
   */
  public static int run(String[] args, PrintStream out, PrintStream err) {
    CliArguments arguments;
    try {
      arguments = CommandLineParser.parse(args);
    } catch (InvalidOptionException e) {
      err.println("error: " + e.getMessage());
      err.println("run " + UsagePrinter.PROGRAM_NAME + " --help for the usage");
      return ExitCode.USAGE_ERROR.status();
    }
    if (arguments.has(CommandLineParser.OPTION_HELP)) {
      UsagePrinter.printUsage(out);
      return ExitCode.SUCCESS.status();
    }
    if (arguments.has(CommandLineParser.OPTION_VERSION)) {
      UsagePrinter.printVersion(out);
      return ExitCode.SUCCESS.status();
    }
    LogLevel level;
    DexRequest request;
    try {
      level = RequestBuilder.resolveLogLevel(arguments);
      request = RequestBuilder.build(arguments);
    } catch (InvalidOptionException e) {
      err.println("error: " + e.getMessage());
      return ExitCode.USAGE_ERROR.status();
    }
    ServiceRegistry registry = new ServiceRegistry(new WrapperConfig(level, false));
    try {
      CompilationReport report = registry.compiler().compile(request);
      if (report instanceof SuccessReport success) {
        printSuccess(out, success, level);
        return ExitCode.SUCCESS.status();
      }
      if (report instanceof FailureReport failure) {
        printFailure(err, failure);
        return ExitCode.COMPILATION_FAILED.status();
      }
      err.println("error: the compilation returned an unknown result");
      return ExitCode.INTERNAL_ERROR.status();
    } catch (InputValidationException e) {
      err.println("error: " + e.getMessage());
      return ExitCode.VALIDATION_ERROR.status();
    } catch (WrapperException e) {
      err.println("error: " + e.getMessage());
      return ExitCode.INTERNAL_ERROR.status();
    }
  }

  /**
   * Prints the result of a compilation that succeeded.
   *
   * @param out the stream the result is printed to
   * @param report the report of the compilation
   * @param level the lowest level of a message that is printed
   */
  private static void printSuccess(PrintStream out, SuccessReport report, LogLevel level) {
    printDiagnostics(out, report.diagnostics(), level);
    for (DexArtifact artifact : report.artifacts()) {
      out.println(
          "wrote "
              + artifact.fileName()
              + " ("
              + artifact.sizeBytes()
              + " bytes, sha256 "
              + artifact.sha256()
              + ")");
    }
    out.println(
        "compiled "
            + report.artifacts().size()
            + " DEX file(s) in "
            + formatDuration(report.duration()));
  }

  /**
   * Prints the result of a compilation that failed.
   *
   * @param err the stream the result is printed to
   * @param report the report of the compilation
   */
  private static void printFailure(PrintStream err, FailureReport report) {
    for (String diagnostic : report.diagnostics()) {
      err.println(diagnostic);
    }
    err.println("error: " + report.reason());
  }

  /**
   * Prints the diagnostics of a compilation.
   *
   * @param out the stream the diagnostics are printed to
   * @param diagnostics the diagnostics of the compilation
   * @param level the lowest level of a message that is printed
   */
  private static void printDiagnostics(PrintStream out, List<String> diagnostics, LogLevel level) {
    for (String diagnostic : diagnostics) {
      String severity = diagnostic.split(":", 2)[0];
      try {
        if (level.includes(LogLevel.fromLabel(severity))) {
          out.println(diagnostic);
        }
      } catch (IllegalArgumentException e) {
        out.println(diagnostic);
      }
    }
  }

  /**
   * Formats a duration for the output.
   *
   * @param duration the duration of the compilation
   * @return the duration in milliseconds
   */
  private static String formatDuration(Duration duration) {
    return duration.toMillis() + " ms";
  }
}
