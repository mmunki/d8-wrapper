package com.mmunki.d8wrapper.service;

import com.mmunki.d8wrapper.exception.InputValidationException;
import com.mmunki.d8wrapper.model.DexOptions;
import com.mmunki.d8wrapper.model.MinSdkLevel;
import com.mmunki.d8wrapper.repository.InputRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Checks a request before it is handed to the dexer.
 *
 * <p>The validator rejects the requests that cannot succeed, it never repairs them. A request that
 * passes the validation is turned into one with absolute paths by the input repository.
 */
public final class RequestValidator {

  /** The name of the directory the DEX files are written to when nothing else is given. */
  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";

  private final InputRepository inputRepository;

  /**
   * Creates the validator.
   *
   * @param inputRepository the repository that resolves the inputs of a request
   */
  public RequestValidator(InputRepository inputRepository) {
    this.inputRepository = inputRepository;
  }

  /**
   * Validates a request.
   *
   * @param options the options of the request
   * @param programInputs the class files, the JAR files and the archives that are compiled
   * @throws InputValidationException when the request cannot succeed
   */
  public void validate(DexOptions options, List<Path> programInputs)
      throws InputValidationException {
    validateMinApiLevel(options.minApiLevel());
    validateOutputDirectory(options.outputDirectory());
    validateInputs(programInputs);
    validateOptionalFiles(options);
  }

  /**
   * Checks that the API level is inside the range that the wrapper supports.
   *
   * @param minApiLevel the API level of the request
   * @throws InputValidationException when the API level is not supported
   */
  private static void validateMinApiLevel(int minApiLevel) throws InputValidationException {
    if (!MinSdkLevel.isSupported(minApiLevel)) {
      throw new InputValidationException(
          "the API level "
              + minApiLevel
              + " is not supported, the range is "
              + MinSdkLevel.MIN_LEVEL
              + " to "
              + MinSdkLevel.MAX_LEVEL);
    }
  }

  /**
   * Checks that the output directory can be used.
   *
   * @param outputDirectory the directory the DEX files are written to
   * @throws InputValidationException when the directory cannot be used
   */
  private static void validateOutputDirectory(Path outputDirectory)
      throws InputValidationException {
    if (outputDirectory == null) {
      throw new InputValidationException("the output directory is missing");
    }
    if (Files.exists(outputDirectory) && !Files.isDirectory(outputDirectory)) {
      throw new InputValidationException(
          "the output path " + outputDirectory + " exists and is not a directory");
    }
  }

  /**
   * Checks that there is something to compile and that every input exists.
   *
   * @param programInputs the inputs of the request
   * @throws InputValidationException when there is no input or one of them is missing
   */
  private void validateInputs(List<Path> programInputs) throws InputValidationException {
    if (programInputs.isEmpty()) {
      throw new InputValidationException("there is nothing to compile, no input was given");
    }
    inputRepository.resolveAll(programInputs);
  }

  /**
   * Checks that the optional files of the request exist.
   *
   * @param options the options of the request
   * @throws InputValidationException when one of the files is missing
   */
  private void validateOptionalFiles(DexOptions options) throws InputValidationException {
    List<Optional<Path>> files =
        List.of(
            options.mainDexList(),
            options.mainDexRules(),
            options.mainDexListOutput(),
            options.proguardMap(),
            options.startupProfile());
    for (Optional<Path> file : files) {
      if (file.isPresent()) {
        inputRepository.resolve(file.get());
      }
    }
  }
}
