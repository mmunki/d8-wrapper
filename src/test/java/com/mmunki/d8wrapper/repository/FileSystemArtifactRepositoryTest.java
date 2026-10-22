package com.mmunki.d8wrapper.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mmunki.d8wrapper.model.DexArtifact;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemArtifactRepositoryTest {

  @TempDir Path directory;

  @Test
  @DisplayName("the DEX files of a directory are collected")
  void collectsTheDexFilesOfADirectory() throws IOException {
    Files.writeString(directory.resolve("classes.dex"), "dex", StandardCharsets.UTF_8);
    Files.writeString(directory.resolve("classes2.dex"), "dex2", StandardCharsets.UTF_8);
    Files.writeString(directory.resolve("notes.txt"), "notes", StandardCharsets.UTF_8);

    List<DexArtifact> artifacts = collect();

    assertEquals(2, artifacts.size());
    assertEquals("classes.dex", artifacts.get(0).fileName());
    assertEquals("classes2.dex", artifacts.get(1).fileName());
  }

  @Test
  @DisplayName("the checksum of a DEX file is the SHA-256 of its bytes")
  void computesTheChecksumOfADexFile() throws IOException {
    Files.write(directory.resolve("classes.dex"), new byte[] {1, 2, 3});

    List<DexArtifact> artifacts = collect();

    assertEquals(1, artifacts.size());
    assertEquals(3, artifacts.get(0).sizeBytes());
    assertEquals(64, artifacts.get(0).sha256().length());
    assertTrue(artifacts.get(0).sha256().matches("[0-9a-f]{64}"));
  }

  @Test
  @DisplayName("a directory without DEX files gives no artifacts")
  void collectsNothingFromAnEmptyDirectory() throws IOException {
    Files.writeString(directory.resolve("notes.txt"), "notes", StandardCharsets.UTF_8);

    assertTrue(collect().isEmpty());
  }

  /**
   * Collects the artifacts of the directory of the test.
   *
   * @return the artifacts of the directory
   * @throws IOException when the directory cannot be read
   */
  private List<DexArtifact> collect() throws IOException {
    return new FileSystemArtifactRepository().collectDexArtifacts(directory);
  }
}
