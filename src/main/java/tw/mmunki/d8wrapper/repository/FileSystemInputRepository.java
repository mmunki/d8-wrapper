package tw.mmunki.d8wrapper.repository;

import tw.mmunki.d8wrapper.exception.InputValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The input repository that reads from the file system.
 *
 * <p>The repository checks the format of the inputs before the dexer sees them, an input that is
 * not a class file and not an archive is a mistake of the user and not a failure of the dexer. The
 * repository is stateless and therefore thread safe.
 */
public final class FileSystemInputRepository implements InputRepository {

  /** The magic number at the beginning of every class file. */
  public static final int CLASS_FILE_MAGIC = 0xCAFEBABE;

  /** The magic number at the beginning of every ZIP archive. */
  public static final byte[] ZIP_MAGIC = {0x50, 0x4B, 0x03, 0x04};

  private static final String CLASS_EXTENSION = ".class";
  private static final List<String> ARCHIVE_EXTENSIONS = List.of(".jar", ".zip", ".apk", ".aar");
  private static final int HEADER_LENGTH = 4;

  @Override
  public Path resolve(Path input) throws InputValidationException {
    if (input == null) {
      throw new InputValidationException("an input path is missing");
    }
    Path resolved = input.toAbsolutePath().normalize();
    if (!Files.exists(resolved)) {
      throw new InputValidationException("the input " + resolved + " does not exist");
    }
    validateFormat(resolved);
    return resolved;
  }

  @Override
  public List<Path> resolveAll(List<Path> inputs) throws InputValidationException {
    List<Path> resolved = new ArrayList<>(inputs.size());
    for (Path input : inputs) {
      resolved.add(resolve(input));
    }
    return List.copyOf(resolved);
  }

  /**
   * Checks that the input is something the dexer can read.
   *
   * @param input the resolved input
   * @throws InputValidationException when the input cannot be read by the dexer
   */
  private static void validateFormat(Path input) throws InputValidationException {
    if (Files.isDirectory(input)) {
      throw new InputValidationException(
          "the input "
              + input
              + " is a directory, the dexer reads class files, JAR files and archives");
    }
    if (!Files.isRegularFile(input)) {
      throw new InputValidationException("the input " + input + " is not a regular file");
    }
    String name = input.getFileName().toString().toLowerCase(Locale.ROOT);
    if (name.endsWith(CLASS_EXTENSION)) {
      validateClassFile(input);
      return;
    }
    if (ARCHIVE_EXTENSIONS.stream().anyMatch(name::endsWith)) {
      validateArchive(input);
    }
  }

  /**
   * Checks that the input is a class file.
   *
   * @param input the resolved input
   * @throws InputValidationException when the input is not a class file
   */
  private static void validateClassFile(Path input) throws InputValidationException {
    byte[] header = header(input);
    int magic =
        ((header[0] & 0xFF) << 24)
            | ((header[1] & 0xFF) << 16)
            | ((header[2] & 0xFF) << 8)
            | (header[3] & 0xFF);
    if (magic != CLASS_FILE_MAGIC) {
      throw new InputValidationException("the input " + input + " is not a class file");
    }
  }

  /**
   * Checks that the input is an archive.
   *
   * @param input the resolved input
   * @throws InputValidationException when the input is not an archive
   */
  private static void validateArchive(Path input) throws InputValidationException {
    byte[] header = header(input);
    for (int index = 0; index < ZIP_MAGIC.length; index++) {
      if (header[index] != ZIP_MAGIC[index]) {
        throw new InputValidationException("the input " + input + " is not an archive");
      }
    }
  }

  /**
   * Reads the first bytes of a file.
   *
   * @param input the file to read
   * @return the first bytes of the file
   * @throws InputValidationException when the file cannot be read or is too short
   */
  private static byte[] header(Path input) throws InputValidationException {
    byte[] header = new byte[HEADER_LENGTH];
    try (InputStream in = Files.newInputStream(input)) {
      int read = in.readNBytes(header, 0, HEADER_LENGTH);
      if (read < HEADER_LENGTH) {
        throw new InputValidationException("the input " + input + " is too short to be read");
      }
    } catch (IOException e) {
      throw new InputValidationException(
          "the input " + input + " could not be read: " + e.getMessage(), e);
    }
    return header;
  }
}
