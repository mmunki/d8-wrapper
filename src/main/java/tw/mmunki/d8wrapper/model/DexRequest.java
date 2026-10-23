package tw.mmunki.d8wrapper.model;

import java.nio.file.Path;
import java.util.List;

/**
 * One request to compile class files into DEX files.
 *
 * @param options the validated options of the compilation
 * @param programInputs the class files, the JAR files and the archives that are compiled
 */
public record DexRequest(DexOptions options, List<Path> programInputs) {

  /**
   * Creates the request and makes the list of inputs immutable.
   *
   * @param options the validated options of the compilation
   * @param programInputs the class files, the JAR files and the archives that are compiled
   */
  public DexRequest {
    programInputs = List.copyOf(programInputs);
  }
}
