package tw.mmunki.d8wrapper.cli;

import tw.mmunki.d8wrapper.exception.InvalidOptionException;
import tw.mmunki.d8wrapper.model.DexOptions;
import tw.mmunki.d8wrapper.model.DexRequest;
import tw.mmunki.d8wrapper.model.LogLevel;
import tw.mmunki.d8wrapper.model.MinSdkLevel;
import tw.mmunki.d8wrapper.service.RequestValidator;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Turns the arguments of the command line into a request.
 *
 * <p>The builder applies the defaults of the wrapper, it checks the values of the options and it
 * rejects the combinations that cannot work. Every message it produces names the option that caused
 * it.
 */
public final class RequestBuilder {

  /** The API level that is used when nothing else is given. */
  public static final int DEFAULT_MIN_API_LEVEL = 21;

  private RequestBuilder() {}

  /**
   * Builds the request of the arguments.
   *
   * @param arguments the parsed arguments of the command line
   * @return the request of the compilation
   * @throws InvalidOptionException when a value of an option cannot be used
   */
  public static DexRequest build(CliArguments arguments) throws InvalidOptionException {
    Path outputDirectory =
        arguments
            .value(CommandLineParser.OPTION_OUTPUT)
            .map(Path::of)
            .orElse(Path.of(RequestValidator.DEFAULT_OUTPUT_DIRECTORY));
    int minApiLevel =
        parseInt(
            arguments.value(CommandLineParser.OPTION_MIN_API),
            CommandLineParser.OPTION_MIN_API,
            DEFAULT_MIN_API_LEVEL);
    if (!MinSdkLevel.isSupported(minApiLevel)) {
      throw new InvalidOptionException(
          "the API level of --"
              + CommandLineParser.OPTION_MIN_API
              + " is "
              + minApiLevel
              + ", the range is "
              + MinSdkLevel.MIN_LEVEL
              + " to "
              + MinSdkLevel.MAX_LEVEL);
    }
    boolean debug = resolveDebug(arguments);
    Optional<Integer> threadCount = resolveThreadCount(arguments);
    DexOptions options =
        new DexOptions(
            outputDirectory,
            minApiLevel,
            debug,
            arguments.has(CommandLineParser.OPTION_NO_DESUGARING),
            arguments.has(CommandLineParser.OPTION_FILE_PER_CLASS),
            arguments.has(CommandLineParser.OPTION_ANDROID_PLATFORM_BUILD),
            threadCount,
            path(arguments, CommandLineParser.OPTION_MAIN_DEX_LIST),
            path(arguments, CommandLineParser.OPTION_MAIN_DEX_RULES),
            path(arguments, CommandLineParser.OPTION_MAIN_DEX_LIST_OUTPUT),
            path(arguments, CommandLineParser.OPTION_PROGUARD_MAP),
            path(arguments, CommandLineParser.OPTION_STARTUP_PROFILE),
            paths(arguments, CommandLineParser.OPTION_LIB),
            paths(arguments, CommandLineParser.OPTION_CLASSPATH));
    List<Path> inputs = arguments.inputs().stream().map(Path::of).toList();
    return new DexRequest(options, inputs);
  }

  /**
   * Resolves the level of the output.
   *
   * @param arguments the parsed arguments of the command line
   * @return the level of the output
   * @throws InvalidOptionException when the level cannot be resolved
   */
  public static LogLevel resolveLogLevel(CliArguments arguments) throws InvalidOptionException {
    boolean quiet = arguments.has(CommandLineParser.OPTION_QUIET);
    boolean verbose = arguments.has(CommandLineParser.OPTION_VERBOSE);
    if (quiet && verbose) {
      throw new InvalidOptionException(
          "the options --"
              + CommandLineParser.OPTION_QUIET
              + " and --"
              + CommandLineParser.OPTION_VERBOSE
              + " cannot be used together");
    }
    if (quiet) {
      return LogLevel.ERROR;
    }
    if (verbose) {
      return LogLevel.DEBUG;
    }
    return LogLevel.DEFAULT;
  }

  /**
   * Resolves the compilation mode.
   *
   * @param arguments the parsed arguments of the command line
   * @return {@code true} when the DEX files keep the debugging information
   * @throws InvalidOptionException when both modes were given
   */
  private static boolean resolveDebug(CliArguments arguments) throws InvalidOptionException {
    boolean debug = arguments.has(CommandLineParser.OPTION_DEBUG);
    boolean release = arguments.has(CommandLineParser.OPTION_RELEASE);
    if (debug && release) {
      throw new InvalidOptionException(
          "the options --"
              + CommandLineParser.OPTION_DEBUG
              + " and --"
              + CommandLineParser.OPTION_RELEASE
              + " cannot be used together");
    }
    return debug;
  }

  /**
   * Reads the value of the option that selects the number of threads.
   *
   * @param arguments the parsed arguments of the command line
   * @return the number of threads, empty when the dexer picks one itself
   * @throws InvalidOptionException when the number is not positive
   */
  private static Optional<Integer> resolveThreadCount(CliArguments arguments)
      throws InvalidOptionException {
    Optional<String> value = arguments.value(CommandLineParser.OPTION_THREAD_COUNT);
    if (value.isEmpty()) {
      return Optional.empty();
    }
    int count = parseInt(value, CommandLineParser.OPTION_THREAD_COUNT, 0);
    if (count <= 0) {
      throw new InvalidOptionException(
          "the value of --"
              + CommandLineParser.OPTION_THREAD_COUNT
              + " has to be positive: "
              + count);
    }
    return Optional.of(count);
  }

  /**
   * Reads the value of an option that holds a path.
   *
   * @param arguments the parsed arguments of the command line
   * @param name the name of the option
   * @return the path of the option, empty when the option was not given
   */
  private static Optional<Path> path(CliArguments arguments, String name) {
    return arguments.value(name).map(Path::of);
  }

  /**
   * Reads the values of an option that holds paths.
   *
   * @param arguments the parsed arguments of the command line
   * @param name the name of the option
   * @return the paths of the option, empty when the option was not given
   */
  private static List<Path> paths(CliArguments arguments, String name) {
    return arguments.values(name).stream().map(Path::of).toList();
  }

  /**
   * Reads the value of an option that holds a number.
   *
   * @param value the value of the option, empty when the option was not given
   * @param name the name of the option
   * @param fallback the value that is used when the option was not given
   * @return the number of the option
   * @throws InvalidOptionException when the value is not a number
   */
  private static int parseInt(Optional<String> value, String name, int fallback)
      throws InvalidOptionException {
    if (value.isEmpty()) {
      return fallback;
    }
    try {
      return Integer.parseInt(value.get().trim());
    } catch (NumberFormatException e) {
      throw new InvalidOptionException(
          "the value of --" + name + " is not a number: " + value.get());
    }
  }
}
