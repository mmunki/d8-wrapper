package tw.mmunki.d8wrapper.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tw.mmunki.d8wrapper.exception.InputValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemInputRepositoryTest {

  @TempDir Path directory;

  @Test
  @DisplayName("an existing file is resolved to its absolute path")
  void resolvesAnExistingFile() throws IOException, InputValidationException {
    Path input =
        Files.write(
            directory.resolve("a.class"),
            new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE});

    Path resolved = new FileSystemInputRepository().resolve(input);

    assertTrue(resolved.isAbsolute());
  }

  @Test
  @DisplayName("a file that is not there is rejected")
  void rejectsAFileThatIsNotThere() {
    InputValidationException failure =
        assertThrows(
            InputValidationException.class,
            () -> new FileSystemInputRepository().resolve(directory.resolve("missing.class")));

    assertTrue(failure.getMessage().contains("does not exist"));
  }

  @Test
  @DisplayName("a file that is not a class file is rejected")
  void rejectsAFileThatIsNotAClassFile() throws IOException {
    Path input = Files.writeString(directory.resolve("a.class"), "not a class file");

    InputValidationException failure =
        assertThrows(
            InputValidationException.class, () -> new FileSystemInputRepository().resolve(input));

    assertTrue(failure.getMessage().contains("is not a class file"));
  }

  @Test
  @DisplayName("a file that is not an archive is rejected")
  void rejectsAFileThatIsNotAnArchive() throws IOException {
    Path input = Files.writeString(directory.resolve("a.jar"), "not an archive");

    InputValidationException failure =
        assertThrows(
            InputValidationException.class, () -> new FileSystemInputRepository().resolve(input));

    assertTrue(failure.getMessage().contains("is not an archive"));
  }

  @Test
  @DisplayName("a directory is rejected")
  void rejectsADirectory() {
    InputValidationException failure =
        assertThrows(
            InputValidationException.class,
            () -> new FileSystemInputRepository().resolve(directory));

    assertTrue(failure.getMessage().contains("is a directory"));
  }

  @Test
  @DisplayName("the class file of the test is accepted")
  void acceptsAClassFile() throws Exception {
    Path input = Path.of(System.getProperty("java.home"), "lib", "jrt-fs.jar");

    assertEquals(
        input.toAbsolutePath().normalize(), new FileSystemInputRepository().resolve(input));
  }
}
