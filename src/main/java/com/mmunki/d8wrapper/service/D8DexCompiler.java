package com.mmunki.d8wrapper.service;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.origin.Origin;
import com.mmunki.d8wrapper.exception.InputValidationException;
import com.mmunki.d8wrapper.model.CompilationReport;
import com.mmunki.d8wrapper.model.DexArtifact;
import com.mmunki.d8wrapper.model.DexOptions;
import com.mmunki.d8wrapper.model.DexRequest;
import com.mmunki.d8wrapper.model.FailureReport;
import com.mmunki.d8wrapper.model.LogLevel;
import com.mmunki.d8wrapper.model.SuccessReport;
import com.mmunki.d8wrapper.repository.ArtifactRepository;
import com.mmunki.d8wrapper.repository.InputRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The compiler that runs the D8 dexer.
 *
 * <p>The class is the only place that talks to the dexer. It builds the command of the dexer from
 * the options of the wrapper, runs it and collects the DEX files that were written. The class is
 * stateless apart from its collaborators and therefore thread safe.
 */
public final class D8DexCompiler implements DexCompiler {

  /** The message of a failure that the dexer reported without a reason of its own. */
  public static final String DEXER_FAILURE = "the D8 dexer failed to compile the inputs";

  private final ArgumentTranslator argumentTranslator;
  private final RequestValidator requestValidator;
  private final ArtifactRepository artifactRepository;
  private final InputRepository inputRepository;
  private final LogLevel level;

  /**
   * Creates the compiler.
   *
   * @param argumentTranslator the translator that builds the arguments of the dexer
   * @param requestValidator the validator that checks a request
   * @param artifactRepository the repository that collects the DEX files
   * @param inputRepository the repository that resolves the inputs of a request
   * @param level the lowest level of a message that is kept
   */
  public D8DexCompiler(
      ArgumentTranslator argumentTranslator,
      RequestValidator requestValidator,
      ArtifactRepository artifactRepository,
      InputRepository inputRepository,
      LogLevel level) {
    this.argumentTranslator = argumentTranslator;
    this.requestValidator = requestValidator;
    this.artifactRepository = artifactRepository;
    this.inputRepository = inputRepository;
    this.level = level;
  }

  @Override
  public CompilationReport compile(DexRequest request) throws InputValidationException {
    Instant started = Instant.now();
    DexOptions options = request.options();
    requestValidator.validate(options, request.programInputs());
    Path outputDirectory = createOutputDirectory(options.outputDirectory());
    List<Path> inputs = inputRepository.resolveAll(request.programInputs());
    DiagnosticsCollector diagnostics = new DiagnosticsCollector(level);
    List<String> programArguments = buildArguments(options, inputs);
    try {
      D8Command command =
          D8Command.parse(programArguments.toArray(new String[0]), Origin.root(), diagnostics)
              .build();
      D8.run(command);
    } catch (CompilationFailedException e) {
      return failure(started, diagnostics, DEXER_FAILURE);
    } catch (RuntimeException e) {
      return failure(started, diagnostics, describe(e));
    }
    return collect(started, diagnostics, outputDirectory);
  }

  /**
   * Builds the arguments of the dexer, the options and the program inputs.
   *
   * @param options the options of the compilation
   * @param inputs the resolved inputs of the compilation
   * @return the arguments of the dexer
   */
  private List<String> buildArguments(DexOptions options, List<Path> inputs) {
    List<String> arguments = new ArrayList<>(argumentTranslator.translate(options));
    inputs.forEach(input -> arguments.add(input.toString()));
    return arguments;
  }

  /**
   * Collects the DEX files that the compilation produced.
   *
   * @param started the moment the compilation started
   * @param diagnostics the diagnostics of the compilation
   * @param outputDirectory the directory the DEX files were written to
   * @return the report of the compilation
   */
  private CompilationReport collect(
      Instant started, DiagnosticsCollector diagnostics, Path outputDirectory) {
    List<DexArtifact> artifacts;
    try {
      artifacts = artifactRepository.collectDexArtifacts(outputDirectory);
    } catch (IOException e) {
      return failure(started, diagnostics, "the DEX files could not be read: " + e.getMessage());
    }
    return new SuccessReport(
        artifacts, diagnostics.messages(), Duration.between(started, Instant.now()));
  }

  /**
   * Builds the report of a compilation that failed.
   *
   * @param started the moment the compilation started
   * @param diagnostics the diagnostics of the compilation
   * @param reason the reason of the failure
   * @return the report of the failure
   */
  private static CompilationReport failure(
      Instant started, DiagnosticsCollector diagnostics, String reason) {
    return new FailureReport(
        reason, diagnostics.messages(), Duration.between(started, Instant.now()));
  }

  /**
   * Describes a failure of the dexer in one line.
   *
   * @param failure the failure of the dexer
   * @return the message of the failure without the stack trace
   */
  private static String describe(RuntimeException failure) {
    String message = failure.getMessage();
    if (message == null || message.isBlank()) {
      return DEXER_FAILURE;
    }
    int end = message.indexOf('\n');
    return end < 0 ? message : message.substring(0, end);
  }

  /**
   * Creates the output directory of a compilation.
   *
   * @param outputDirectory the directory the DEX files are written to
   * @return the directory that was created
   * @throws InputValidationException when the directory cannot be created
   */
  private static Path createOutputDirectory(Path outputDirectory) throws InputValidationException {
    try {
      Files.createDirectories(outputDirectory);
    } catch (IOException e) {
      throw new InputValidationException(
          "the output directory " + outputDirectory + " could not be created: " + e.getMessage(),
          e);
    }
    return outputDirectory;
  }
}
