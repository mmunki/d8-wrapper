package com.mmunki.d8wrapper.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * The validated options of one compilation.
 *
 * <p>Every value in this record has already been checked, the record itself only guarantees that
 * the collections it holds cannot be changed from the outside.
 *
 * @param outputDirectory the directory the DEX files are written to
 * @param minApiLevel the lowest Android API level the DEX files have to run on
 * @param debug {@code true} when the DEX files keep the debugging information
 * @param disableDesugaring {@code true} when the desugaring step is skipped
 * @param filePerClass {@code true} when every input class becomes its own DEX file
 * @param androidPlatformBuild {@code true} when the build is a platform build
 * @param threadCount the number of threads to use, empty means the D8 default
 * @param mainDexList the file that lists the classes of the primary DEX file
 * @param mainDexRules the ProGuard rules that select the classes of the primary DEX file
 * @param mainDexListOutput the file the resulting main dex list is written to
 * @param proguardMap the mapping file that is used for the distribution build
 * @param startupProfile the ART profile that shapes the layout of the DEX files
 * @param libraryInputs the library inputs, the Android runtime and the libraries of the app
 * @param classpathInputs the compile time classpath of the inputs
 */
public record DexOptions(
    Path outputDirectory,
    int minApiLevel,
    boolean debug,
    boolean disableDesugaring,
    boolean filePerClass,
    boolean androidPlatformBuild,
    Optional<Integer> threadCount,
    Optional<Path> mainDexList,
    Optional<Path> mainDexRules,
    Optional<Path> mainDexListOutput,
    Optional<Path> proguardMap,
    Optional<Path> startupProfile,
    List<Path> libraryInputs,
    List<Path> classpathInputs) {

  /**
   * Creates the options and makes the collections immutable.
   *
   * @param outputDirectory the directory the DEX files are written to
   * @param minApiLevel the lowest Android API level
   * @param debug {@code true} when the DEX files keep the debugging information
   * @param disableDesugaring {@code true} when the desugaring step is skipped
   * @param filePerClass {@code true} when every input class becomes its own DEX file
   * @param androidPlatformBuild {@code true} when the build is a platform build
   * @param threadCount the number of threads to use
   * @param mainDexList the file that lists the classes of the primary DEX file
   * @param mainDexRules the ProGuard rules of the primary DEX file
   * @param mainDexListOutput the file the resulting main dex list is written to
   * @param proguardMap the mapping file of the distribution build
   * @param startupProfile the ART profile of the build
   * @param libraryInputs the library inputs
   * @param classpathInputs the compile time classpath
   */
  public DexOptions {
    libraryInputs = List.copyOf(libraryInputs);
    classpathInputs = List.copyOf(classpathInputs);
  }

  /**
   * Returns the name of the platform release of the configured API level.
   *
   * @return the platform name or {@code null} when the level has no name
   */
  public String platformName() {
    return MinSdkLevel.platformNameOf(minApiLevel);
  }
}
