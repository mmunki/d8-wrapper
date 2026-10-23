package tw.mmunki.d8wrapper.cli;

import tw.mmunki.d8wrapper.exception.InvalidOptionException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses the command line of the wrapper.
 *
 * <p>The parser only knows the syntax of the command line, the meaning of the options is checked
 * when the arguments are turned into a request. An option is written as {@code --name} or {@code
 * --name=value}, an option that needs a value can also be written as {@code --name value}.
 * Everything after {@code --} is an input.
 */
public final class CommandLineParser {

  /** The name of the option that selects the output directory. */
  public static final String OPTION_OUTPUT = "output";

  /** The name of the option that selects the lowest supported API level. */
  public static final String OPTION_MIN_API = "min-api";

  /** The name of the option that compiles with the debugging information. */
  public static final String OPTION_DEBUG = "debug";

  /** The name of the option that compiles without the debugging information. */
  public static final String OPTION_RELEASE = "release";

  /** The name of the option that turns the desugaring off. */
  public static final String OPTION_NO_DESUGARING = "no-desugaring";

  /** The name of the option that writes one DEX file per class. */
  public static final String OPTION_FILE_PER_CLASS = "file-per-class";

  /** The name of the option that turns the build into a platform build. */
  public static final String OPTION_ANDROID_PLATFORM_BUILD = "android-platform-build";

  /** The name of the option that selects the number of threads. */
  public static final String OPTION_THREAD_COUNT = "thread-count";

  /** The name of the option that selects the file with the classes of the primary DEX file. */
  public static final String OPTION_MAIN_DEX_LIST = "main-dex-list";

  /** The name of the option that selects the rules of the primary DEX file. */
  public static final String OPTION_MAIN_DEX_RULES = "main-dex-rules";

  /** The name of the option that selects the file the main dex list is written to. */
  public static final String OPTION_MAIN_DEX_LIST_OUTPUT = "main-dex-list-output";

  /** The name of the option that selects the mapping file of the distribution build. */
  public static final String OPTION_PROGUARD_MAP = "pg-map";

  /** The name of the option that selects the ART profile of the build. */
  public static final String OPTION_STARTUP_PROFILE = "startup-profile";

  /** The name of the option that adds a library input. */
  public static final String OPTION_LIB = "lib";

  /** The name of the option that adds a compile time classpath entry. */
  public static final String OPTION_CLASSPATH = "classpath";

  /** The name of the option that lowers the level of the output. */
  public static final String OPTION_QUIET = "quiet";

  /** The name of the option that raises the level of the output. */
  public static final String OPTION_VERBOSE = "verbose";

  /** The name of the option that prints the usage. */
  public static final String OPTION_HELP = "help";

  /** The name of the option that prints the versions. */
  public static final String OPTION_VERSION = "version";

  /** The prefix of every option of the command line. */
  public static final String OPTION_PREFIX = "--";

  /** The argument that ends the options of the command line. */
  public static final String END_OF_OPTIONS = "--";

  private static final Set<String> FLAGS =
      Set.of(
          OPTION_DEBUG,
          OPTION_RELEASE,
          OPTION_NO_DESUGARING,
          OPTION_FILE_PER_CLASS,
          OPTION_ANDROID_PLATFORM_BUILD,
          OPTION_QUIET,
          OPTION_VERBOSE,
          OPTION_HELP,
          OPTION_VERSION);

  private static final Set<String> OPTIONS_WITH_VALUE =
      Set.of(
          OPTION_OUTPUT,
          OPTION_MIN_API,
          OPTION_THREAD_COUNT,
          OPTION_MAIN_DEX_LIST,
          OPTION_MAIN_DEX_RULES,
          OPTION_MAIN_DEX_LIST_OUTPUT,
          OPTION_PROGUARD_MAP,
          OPTION_STARTUP_PROFILE,
          OPTION_LIB,
          OPTION_CLASSPATH);

  private CommandLineParser() {}

  /**
   * Parses the arguments of the command line.
   *
   * @param arguments the arguments of the command line
   * @return the parsed arguments
   * @throws InvalidOptionException when an option is unknown or misses its value
   */
  public static CliArguments parse(String[] arguments) throws InvalidOptionException {
    Map<String, List<String>> options = new LinkedHashMap<>();
    List<String> inputs = new ArrayList<>();
    boolean optionsEnded = false;
    for (int index = 0; index < arguments.length; index++) {
      String argument = arguments[index];
      if (optionsEnded) {
        inputs.add(argument);
        continue;
      }
      if (END_OF_OPTIONS.equals(argument)) {
        optionsEnded = true;
        continue;
      }
      if (!argument.startsWith(OPTION_PREFIX)) {
        inputs.add(argument);
        continue;
      }
      String name = argument.substring(OPTION_PREFIX.length());
      String inlineValue = null;
      int separator = name.indexOf('=');
      if (separator >= 0) {
        inlineValue = name.substring(separator + 1);
        name = name.substring(0, separator);
      }
      validateOption(name);
      if (FLAGS.contains(name)) {
        if (inlineValue != null) {
          throw new InvalidOptionException("the option --" + name + " does not take a value");
        }
        options.computeIfAbsent(name, key -> new ArrayList<>());
        continue;
      }
      String value = inlineValue;
      if (value == null) {
        if (index + 1 >= arguments.length) {
          throw new InvalidOptionException("the option --" + name + " needs a value");
        }
        value = arguments[++index];
      }
      options.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }
    return new CliArguments(options, inputs);
  }

  /**
   * Checks that the wrapper knows the option.
   *
   * @param name the name of the option, without its dashes
   * @throws InvalidOptionException when the option is unknown
   */
  private static void validateOption(String name) throws InvalidOptionException {
    if (name.isEmpty()) {
      throw new InvalidOptionException("an option without a name was given");
    }
    if (!FLAGS.contains(name) && !OPTIONS_WITH_VALUE.contains(name)) {
      throw new InvalidOptionException("unknown option: --" + name);
    }
  }
}
