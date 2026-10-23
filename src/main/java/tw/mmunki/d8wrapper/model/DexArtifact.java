package tw.mmunki.d8wrapper.model;

import java.nio.file.Path;

/**
 * One DEX file that the compilation produced.
 *
 * @param path the path of the file
 * @param sizeBytes the size of the file in bytes
 * @param sha256 the SHA-256 checksum of the file in lower case hexadecimal
 */
public record DexArtifact(Path path, long sizeBytes, String sha256) {

  /** The extension of a DEX file. */
  public static final String DEX_EXTENSION = ".dex";

  /**
   * Returns the name of the file.
   *
   * @return the file name
   */
  public String fileName() {
    return path.getFileName().toString();
  }
}
