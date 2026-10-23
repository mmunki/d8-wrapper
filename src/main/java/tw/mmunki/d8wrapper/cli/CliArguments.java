package tw.mmunki.d8wrapper.cli;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The arguments of the command line, after they were parsed.
 *
 * <p>The record keeps the options as they were written on the command line, the values of an option
 * that can be used more than once are kept in the order they were given.
 *
 * @param options the options of the command line, the name of an option without its dashes
 * @param inputs the inputs of the compilation, the arguments that are no options
 */
public record CliArguments(Map<String, List<String>> options, List<String> inputs) {

  /**
   * Creates the arguments and makes the collections immutable.
   *
   * @param options the options of the command line
   * @param inputs the inputs of the compilation
   */
  public CliArguments {
    options = Map.copyOf(options);
    inputs = List.copyOf(inputs);
  }

  /**
   * Tells whether an option was given.
   *
   * @param name the name of the option, without its dashes
   * @return {@code true} when the option was given
   */
  public boolean has(String name) {
    return options.containsKey(name);
  }

  /**
   * Returns the values of an option.
   *
   * @param name the name of the option, without its dashes
   * @return the values of the option, empty when the option was not given
   */
  public List<String> values(String name) {
    return options.getOrDefault(name, List.of());
  }

  /**
   * Returns the single value of an option.
   *
   * @param name the name of the option, without its dashes
   * @return the value of the option, empty when the option was not given
   */
  public Optional<String> value(String name) {
    List<String> values = values(name);
    return values.isEmpty() ? Optional.empty() : Optional.of(values.get(values.size() - 1));
  }
}
