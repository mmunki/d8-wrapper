package tw.mmunki.d8wrapper.service;

import tw.mmunki.d8wrapper.model.DexOptions;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Translates the options of the wrapper into the arguments of the D8 dexer.
 *
 * <p>The wrapper has its own, stable command line. The translation is the only place that knows the
 * flags of the dexer, so a change of the dexer only touches this class.
 */
public final class ArgumentTranslator {

  /** The flag that selects the output of the dexer. */
  public static final String FLAG_OUTPUT = "--output";

  /** The flag that selects the lowest supported API level. */
  public static final String FLAG_MIN_API = "--min-api";

  /** The flag that compiles with the debugging information. */
  public static final String FLAG_DEBUG = "--debug";

  /** The flag that compiles without the debugging information. */
  public static final String FLAG_RELEASE = "--release";

  /** The flag that turns the desugaring off. */
  public static final String FLAG_NO_DESUGARING = "--no-desugaring";

  /** The flag that writes one DEX file per class. */
  public static final String FLAG_FILE_PER_CLASS = "--file-per-class";

  /** The flag that turns the build into a platform build. */
  public static final String FLAG_ANDROID_PLATFORM_BUILD = "--android-platform-build";

  /** The flag that selects the number of threads. */
  public static final String FLAG_THREAD_COUNT = "--thread-count";

  /** The flag that selects the file with the classes of the primary DEX file. */
  public static final String FLAG_MAIN_DEX_LIST = "--main-dex-list";

  /** The flag that selects the rules of the primary DEX file. */
  public static final String FLAG_MAIN_DEX_RULES = "--main-dex-rules";

  /** The flag that selects the file the main dex list is written to. */
  public static final String FLAG_MAIN_DEX_LIST_OUTPUT = "--main-dex-list-output";

  /** The flag that selects the mapping file of the distribution build. */
  public static final String FLAG_PROGUARD_MAP = "--pg-map";

  /** The flag that selects the ART profile of the build. */
  public static final String FLAG_STARTUP_PROFILE = "--startup-profile";

  /** The flag that adds a library input. */
  public static final String FLAG_LIB = "--lib";

  /** The flag that adds a compile time classpath entry. */
  public static final String FLAG_CLASSPATH = "--classpath";

  /** Creates the translator, it is stateless and therefore thread safe. */
  public ArgumentTranslator() {}

  /**
   * Translates the options into the arguments of the dexer.
   *
   * @param options the options of the compilation
   * @return the arguments of the dexer, the program inputs are not part of them
   */
  public List<String> translate(DexOptions options) {
    List<String> arguments = new ArrayList<>();
    arguments.add(FLAG_MIN_API);
    arguments.add(Integer.toString(options.minApiLevel()));
    arguments.add(options.debug() ? FLAG_DEBUG : FLAG_RELEASE);
    arguments.add(FLAG_OUTPUT);
    arguments.add(options.outputDirectory().toString());
    if (options.disableDesugaring()) {
      arguments.add(FLAG_NO_DESUGARING);
    }
    if (options.filePerClass()) {
      arguments.add(FLAG_FILE_PER_CLASS);
    }
    if (options.androidPlatformBuild()) {
      arguments.add(FLAG_ANDROID_PLATFORM_BUILD);
    }
    options
        .threadCount()
        .ifPresent(
            count -> {
              arguments.add(FLAG_THREAD_COUNT);
              arguments.add(Integer.toString(count));
            });
    addPath(arguments, FLAG_MAIN_DEX_LIST, options.mainDexList());
    addPath(arguments, FLAG_MAIN_DEX_RULES, options.mainDexRules());
    addPath(arguments, FLAG_MAIN_DEX_LIST_OUTPUT, options.mainDexListOutput());
    addPath(arguments, FLAG_PROGUARD_MAP, options.proguardMap());
    addPath(arguments, FLAG_STARTUP_PROFILE, options.startupProfile());
    for (Path library : options.libraryInputs()) {
      addPath(arguments, FLAG_LIB, Optional.of(library));
    }
    for (Path classpath : options.classpathInputs()) {
      addPath(arguments, FLAG_CLASSPATH, Optional.of(classpath));
    }
    return List.copyOf(arguments);
  }

  /**
   * Adds a flag and its value to the arguments, when there is a value.
   *
   * @param arguments the arguments of the dexer
   * @param flag the flag to add
   * @param value the value of the flag, empty when the flag is not used
   */
  private static void addPath(List<String> arguments, String flag, Optional<Path> value) {
    value.ifPresent(
        path -> {
          arguments.add(flag);
          arguments.add(path.toString());
        });
  }
}
