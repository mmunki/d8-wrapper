package com.mmunki.d8wrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mmunki.d8wrapper.model.DexOptions;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArgumentTranslatorTest {

  private static final Path OUTPUT = Path.of("out");
  private static final Path LIBRARY = Path.of("android.jar");

  @Test
  @DisplayName("the output, the API level and the mode are translated")
  void translatesTheOutputTheApiLevelAndTheMode() {
    List<String> arguments = translate(options(false));

    assertEquals(
        List.of("--min-api", "21", "--release", "--output", "out"), arguments.subList(0, 5));
  }

  @Test
  @DisplayName("the debug mode is translated")
  void translatesTheDebugMode() {
    List<String> arguments = translate(options(true));

    assertTrue(arguments.contains("--debug"));
    assertFalse(arguments.contains("--release"));
  }

  @Test
  @DisplayName("the libraries and the classpath are translated")
  void translatesTheLibrariesAndTheClasspath() {
    DexOptions options =
        new DexOptions(
            OUTPUT,
            21,
            false,
            false,
            false,
            false,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            List.of(LIBRARY),
            List.of());

    List<String> arguments = translate(options);

    assertEquals("--lib", arguments.get(arguments.size() - 2));
    assertEquals(LIBRARY.toString(), arguments.get(arguments.size() - 1));
  }

  /**
   * Translates the options of a test.
   *
   * @param options the options of the test
   * @return the arguments of the dexer
   */
  private static List<String> translate(DexOptions options) {
    return new ArgumentTranslator().translate(options);
  }

  /**
   * Builds the options of a test.
   *
   * @param debug {@code true} when the DEX files keep the debugging information
   * @return the options of the test
   */
  private static DexOptions options(boolean debug) {
    return new DexOptions(
        OUTPUT,
        21,
        debug,
        false,
        false,
        false,
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        List.of(),
        List.of());
  }
}
