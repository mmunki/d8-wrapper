package tw.mmunki.d8wrapper.cli;

import com.android.tools.r8.Version;
import java.io.PrintStream;

/**
 * Prints the usage and the versions of the wrapper.
 *
 * <p>The text of the usage is the only description of the command line, it is written in one place
 * so that it cannot drift away from the options that the parser knows.
 */
public final class UsagePrinter {

  /** The name of the program, it is used in the usage. */
  public static final String PROGRAM_NAME = "d8-wrapper";

  /** The version that is used when the wrapper does not run from its JAR. */
  public static final String UNKNOWN_VERSION = "development";

  private UsagePrinter() {}

  /**
   * Prints the usage of the wrapper.
   *
   * @param out the stream the usage is printed to
   */
  public static void printUsage(PrintStream out) {
    out.println("usage: " + PROGRAM_NAME + " [options] <input>...");
    out.println();
    out.println("Compiles class files, JAR files and archives into DEX files with the D8 dexer.");
    out.println();
    out.println("options:");
    out.println("  --output <directory>       the directory the DEX files are written to");
    out.println("                             (default: out)");
    out.println("  --min-api <level>          the lowest Android API level the DEX files have to");
    out.println("                             run on (default: 21)");
    out.println("  --debug                    keep the debugging information in the DEX files");
    out.println("  --release                  drop the debugging information (the default)");
    out.println("  --no-desugaring            skip the desugaring step");
    out.println("  --file-per-class           write one DEX file per class");
    out.println("  --android-platform-build   compile as a platform build");
    out.println("  --thread-count <number>    the number of threads the dexer uses");
    out.println("  --lib <path>               add a library input, can be used more than once");
    out.println("  --classpath <path>         add a compile time classpath entry, can be used");
    out.println("                             more than once");
    out.println("  --main-dex-list <file>     the file that lists the classes of the primary DEX");
    out.println("                             file");
    out.println("  --main-dex-rules <file>    the ProGuard rules that select the classes of the");
    out.println("                             primary DEX file");
    out.println("  --main-dex-list-output <file>");
    out.println("                             the file the resulting main dex list is written to");
    out.println("  --pg-map <file>            the mapping file of a distribution build");
    out.println("  --startup-profile <file>   the ART profile that shapes the DEX files");
    out.println("  --quiet                    print the errors only");
    out.println("  --verbose                  print everything, including the diagnostics");
    out.println("  --help                     print this text");
    out.println("  --version                  print the versions of the wrapper and the dexer");
    out.println();
    out.println("exit codes:");
    out.println("  0   the DEX files were written");
    out.println("  2   the command line could not be understood");
    out.println("  3   the request cannot succeed");
    out.println("  4   the dexer refused to compile the inputs");
    out.println("  70  the wrapper hit a failure it does not know about");
  }

  /**
   * Prints the versions of the wrapper and of the dexer.
   *
   * @param out the stream the versions are printed to
   */
  public static void printVersion(PrintStream out) {
    out.println(PROGRAM_NAME + " " + wrapperVersion());
    out.println("D8 " + dexerVersion());
  }

  /**
   * Returns the version of the D8 dexer.
   *
   * @return the version of the dexer, or a message that says how to make it available
   */
  private static String dexerVersion() {
    try {
      return Version.getVersionString();
    } catch (NoClassDefFoundError e) {
      return "not available (put d8.jar next to d8-wrapper.jar)";
    }
  }

  /**
   * Returns the version of the wrapper.
   *
   * @return the version of the wrapper, the development version when it does not run from its JAR
   */
  public static String wrapperVersion() {
    String version = Main.class.getPackage().getImplementationVersion();
    return version == null ? UNKNOWN_VERSION : version;
  }
}
