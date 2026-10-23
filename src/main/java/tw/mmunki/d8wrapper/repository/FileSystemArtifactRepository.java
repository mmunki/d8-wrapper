package tw.mmunki.d8wrapper.repository;

import tw.mmunki.d8wrapper.model.DexArtifact;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * The artifact repository that reads the DEX files from the file system.
 *
 * <p>The repository computes the SHA-256 checksum of every file it finds, the checksum is what
 * makes a published artifact verifiable.
 */
public final class FileSystemArtifactRepository implements ArtifactRepository {

  /** The algorithm that is used for the checksums of the artifacts. */
  public static final String CHECKSUM_ALGORITHM = "SHA-256";

  private static final String HEX_FORMAT = "%02x";

  @Override
  public List<DexArtifact> collectDexArtifacts(Path outputDirectory) throws IOException {
    List<DexArtifact> artifacts = new ArrayList<>();
    if (!Files.isDirectory(outputDirectory)) {
      return artifacts;
    }
    try (DirectoryStream<Path> entries = Files.newDirectoryStream(outputDirectory)) {
      for (Path entry : entries) {
        if (isDexFile(entry)) {
          artifacts.add(new DexArtifact(entry, Files.size(entry), checksum(entry)));
        }
      }
    }
    artifacts.sort((left, right) -> left.path().compareTo(right.path()));
    return List.copyOf(artifacts);
  }

  /**
   * Tells whether a path is a DEX file.
   *
   * @param path the path to check
   * @return {@code true} when the path is a regular file with the DEX extension
   */
  private static boolean isDexFile(Path path) {
    return Files.isRegularFile(path)
        && path.getFileName().toString().endsWith(DexArtifact.DEX_EXTENSION);
  }

  /**
   * Computes the checksum of a file.
   *
   * @param path the file to read
   * @return the checksum in lower case hexadecimal
   * @throws IOException when the file cannot be read
   */
  private static String checksum(Path path) throws IOException {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance(CHECKSUM_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new IOException("the algorithm " + CHECKSUM_ALGORITHM + " is not available", e);
    }
    byte[] buffer = new byte[8192];
    try (InputStream in = Files.newInputStream(path);
        DigestInputStream digestStream = new DigestInputStream(in, digest)) {
      while (digestStream.read(buffer) != -1) {
        // The digest is updated by the stream itself.
      }
    }
    return toHex(digest.digest());
  }

  /**
   * Formats the bytes of a checksum as lower case hexadecimal.
   *
   * @param bytes the bytes of the checksum
   * @return the checksum as a string
   */
  private static String toHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder(bytes.length * 2);
    for (byte value : bytes) {
      builder.append(String.format(HEX_FORMAT, value));
    }
    return builder.toString();
  }
}
