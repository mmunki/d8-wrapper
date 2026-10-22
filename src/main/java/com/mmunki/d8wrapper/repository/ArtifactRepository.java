package com.mmunki.d8wrapper.repository;

import com.mmunki.d8wrapper.model.DexArtifact;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Finds the files that a compilation produced.
 *
 * <p>The interface is the boundary between the compilation and the file system, the service that
 * compiles does not know how the artifacts are stored.
 */
public interface ArtifactRepository {

  /**
   * Collects the DEX files of an output directory.
   *
   * @param outputDirectory the directory the DEX files were written to
   * @return the DEX files of the directory, empty when there are none
   * @throws IOException when the directory cannot be read
   */
  List<DexArtifact> collectDexArtifacts(Path outputDirectory) throws java.io.IOException;
}
