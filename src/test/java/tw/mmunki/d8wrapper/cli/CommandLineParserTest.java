package tw.mmunki.d8wrapper.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tw.mmunki.d8wrapper.exception.InvalidOptionException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommandLineParserTest {

  @Test
  @DisplayName("an option with a value is parsed")
  void parsesAnOptionWithAValue() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--output", "build", "classes"});

    assertEquals(List.of("build"), arguments.values(CommandLineParser.OPTION_OUTPUT));
    assertEquals(List.of("classes"), arguments.inputs());
  }

  @Test
  @DisplayName("an option with an inline value is parsed")
  void parsesAnOptionWithAnInlineValue() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--output=build"});

    assertEquals(List.of("build"), arguments.values(CommandLineParser.OPTION_OUTPUT));
  }

  @Test
  @DisplayName("an option that can be used more than once keeps its values")
  void parsesAnOptionThatIsUsedMoreThanOnce() throws InvalidOptionException {
    CliArguments arguments =
        CommandLineParser.parse(new String[] {"--lib", "one.jar", "--lib", "two.jar"});

    assertEquals(List.of("one.jar", "two.jar"), arguments.values(CommandLineParser.OPTION_LIB));
  }

  @Test
  @DisplayName("a flag is parsed without a value")
  void parsesAFlag() throws InvalidOptionException {
    CliArguments arguments = CommandLineParser.parse(new String[] {"--debug"});

    assertTrue(arguments.has(CommandLineParser.OPTION_DEBUG));
    assertEquals(List.of(), arguments.values(CommandLineParser.OPTION_DEBUG));
  }

  @Test
  @DisplayName("everything after the end of the options is an input")
  void parsesTheInputsAfterTheEndOfTheOptions() throws InvalidOptionException {
    CliArguments arguments =
        CommandLineParser.parse(new String[] {"--debug", "--", "--not-an-option"});

    assertEquals(List.of("--not-an-option"), arguments.inputs());
  }

  @Test
  @DisplayName("an unknown option is rejected")
  void rejectsAnUnknownOption() {
    InvalidOptionException failure =
        assertThrows(
            InvalidOptionException.class,
            () -> CommandLineParser.parse(new String[] {"--nonsense"}));

    assertTrue(failure.getMessage().contains("--nonsense"));
  }

  @Test
  @DisplayName("an option that misses its value is rejected")
  void rejectsAnOptionThatMissesItsValue() {
    assertThrows(
        InvalidOptionException.class, () -> CommandLineParser.parse(new String[] {"--output"}));
  }
}
