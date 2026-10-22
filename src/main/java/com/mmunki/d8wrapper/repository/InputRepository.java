package com.mmunki.d8wrapper.repository;

import com.mmunki.d8wrapper.exception.InputValidationException;
import java.nio.file.Path;
import java.util.List;

/**
 * Resolves the inputs of a compilation.
 *
 * <p>An input that does not exist is an error and not a warning, the compilation of a file that is
 * not there cannot succeed.
 */
public interface InputRepository {

  /**
   * Resolves one input and checks that it exists.
   *
   * @param input the path of the input, relative paths are resolved against the working directory
   * @return the absolute path of the input
   * @throws InputValidationException when the input does not exist
   */
  Path resolve(Path input) throws InputValidationException;

  /**
   * Resolves every input and checks that they exist.
   *
   * @param inputs the paths of the inputs
   * @return the absolute paths of the inputs
   * @throws InputValidationException when one of the inputs does not exist
   */
  List<Path> resolveAll(List<Path> inputs) throws InputValidationException;
}
