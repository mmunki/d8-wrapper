package tw.mmunki.d8wrapper.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tw.mmunki.d8wrapper.exception.InvalidOptionException;
import tw.mmunki.d8wrapper.model.DexRequest;
import tw.mmunki.d8wrapper.model.LogLevel;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBuilderTest {

  @Test
  @DisplayName("the defaults of the wrapper are used")
  void usesTheDefaults() throws InvalidOptionException {
    DexRequest request = RequestBuilder.build(CommandLineParser.parse(new String[] {"a.jar"}));

    assertEquals("out", request.options().outputDirectory().toString());
    assertEquals(RequestBuilder.DEFAULT_MIN_API_LEVEL, request.options().minApiLevel());
    assertFalse(request.options().debug());
    assertEquals(
        List.of("a.jar"), request.programInputs().stream().map(path -> path.toString()).toList());
  }

  @Test
  @DisplayName("the debug mode is kept")
  void keepsTheDebugMode() throws InvalidOptionException {
    DexRequest request =
        RequestBuilder.build(CommandLineParser.parse(new String[] {"--debug", "a.jar"}));

    assertTrue(request.options().debug());
  }

  @Test
  @DisplayName("the debug mode and the release mode cannot be used together")
  void rejectsTheDebugModeAndTheReleaseModeTogether() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--debug", "--release"});

    assertThrows(InvalidOptionException.class, () -> RequestBuilder.build(arguments));
  }

  @Test
  @DisplayName("an API level outside of the supported range is rejected")
  void rejectsAnApiLevelOutsideOfTheRange() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--min-api", "99", "a.jar"});

    assertThrows(InvalidOptionException.class, () -> RequestBuilder.build(arguments));
  }

  @Test
  @DisplayName("a number that is not a number is rejected")
  void rejectsAValueThatIsNotANumber() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--min-api", "many", "a.jar"});

    assertThrows(InvalidOptionException.class, () -> RequestBuilder.build(arguments));
  }

  @Test
  @DisplayName("a thread count that is not positive is rejected")
  void rejectsAThreadCountThatIsNotPositive() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--thread-count", "0", "a.jar"});

    assertThrows(InvalidOptionException.class, () -> RequestBuilder.build(arguments));
  }

  @Test
  @DisplayName("the quiet and the verbose mode cannot be used together")
  void rejectsTheQuietAndTheVerboseModeTogether() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--quiet", "--verbose"});

    assertThrows(InvalidOptionException.class, () -> RequestBuilder.resolveLogLevel(arguments));
  }

  @Test
  @DisplayName("the level of the output follows the quiet and the verbose mode")
  void resolvesTheLevelOfTheOutput() throws InvalidOptionException {
    assertEquals(
        LogLevel.ERROR,
        RequestBuilder.resolveLogLevel(CommandLineParser.parse(new String[] {"--quiet"})));
    assertEquals(
        LogLevel.DEBUG,
        RequestBuilder.resolveLogLevel(CommandLineParser.parse(new String[] {"--verbose"})));
  }
}
