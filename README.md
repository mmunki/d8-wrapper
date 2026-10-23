# d8-wrapper

A production-ready Java wrapper around the **D8 dexer**, the dexer of the
Android build tools.

The wrapper turns the command line of the dexer into a small, validated and
stable interface, collects the DEX files that were written and reports the
SHA-256 checksum of every one of them.

## Artifacts

| Artifact | Contents | Use it when |
| --- | --- | --- |
| `d8-wrapper.jar` | the wrapper only, **no D8 classes** | you already have `d8.jar` |
| `d8.jar` | the D8 dexer (`com.android.tools:r8`) | you use `d8-wrapper.jar` |
| `d8-wrapper-all.jar` | the wrapper and D8 in one JAR | you want a single file |

`d8-wrapper.jar` declares `Class-Path: d8.jar` in its manifest.  Put the two
files in the same directory and run:

```bash
java -jar d8-wrapper.jar --min-api 21 --output out build/classes
```

Or use the self-contained JAR:

```bash
java -jar d8-wrapper-all.jar --min-api 21 --output out build/classes
```

## What it does

- validates the command line before anything is compiled; every message names
  the option that caused it
- compiles class files, JAR files and archives into DEX files
- collects the DEX files of the output directory and computes the SHA-256
  checksum of each one
- returns an exit code that says what happened, the codes are listed below

## Usage

```
usage: d8-wrapper [options] <input>...

options:
  --output <directory>       the directory the DEX files are written to (default: out)
  --min-api <level>          the lowest Android API level the DEX files have to run on
                             (default: 21)
  --debug                    keep the debugging information in the DEX files
  --release                  drop the debugging information (the default)
  --no-desugaring            skip the desugaring step
  --file-per-class           write one DEX file per class
  --android-platform-build   compile as a platform build
  --thread-count <number>    the number of threads the dexer uses
  --lib <path>               add a library input, can be used more than once
  --classpath <path>         add a compile time classpath entry, can be used more than once
  --main-dex-list <file>     the file that lists the classes of the primary DEX file
  --main-dex-rules <file>    the ProGuard rules that select the classes of the primary DEX file
  --main-dex-list-output <file>
                             the file the resulting main dex list is written to
  --pg-map <file>            the mapping file of a distribution build
  --startup-profile <file>   the ART profile that shapes the DEX files
  --quiet                    print the errors only
  --verbose                  print everything, including the diagnostics
  --help                     print this text
  --version                  print the versions of the wrapper and the dexer
```

An option is written as `--name` or `--name=value`; an option that needs a
value can also be written as `--name value`.  Everything after `--` is an
input.

### Exit codes

| Code | Meaning |
| --- | --- |
| 0 | the DEX files were written |
| 2 | the command line could not be understood |
| 3 | the request cannot succeed |
| 4 | the dexer refused to compile the inputs |
| 70 | the wrapper hit a failure it does not know about |

## Architecture

The wrapper is layered; every layer only knows the layer below it.

| Package | Responsibility |
| --- | --- |
| `tw.mmunki.d8wrapper.cli` | entry point, command-line parser and usage |
| `tw.mmunki.d8wrapper.config` | configuration and dependency injection |
| `tw.mmunki.d8wrapper.model` | immutable records, enums and the sealed report |
| `tw.mmunki.d8wrapper.repository` | access to inputs and generated artifacts |
| `tw.mmunki.d8wrapper.service` | compilation, validation and diagnostics |
| `tw.mmunki.d8wrapper.exception` | checked domain exceptions |

The code follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html),
uses Java 17 and keeps every class small and focused.  The interfaces are the
boundaries; the compiler is programmed against `DexCompiler`, not against D8.

```
src/main/java/tw/mmunki/d8wrapper
├── cli
│   ├── CliArguments.java
│   ├── CommandLineParser.java
│   ├── ExitCode.java
│   ├── Main.java
│   ├── RequestBuilder.java
│   ├── UsagePrinter.java
│   └── package-info.java
├── config
│   ├── ConfigLoader.java
│   ├── ServiceRegistry.java
│   ├── SystemPropertyConfigLoader.java
│   ├── WrapperConfig.java
│   └── package-info.java
├── exception
│   ├── CompilationFailedException.java
│   ├── InputValidationException.java
│   ├── InvalidOptionException.java
│   ├── WrapperException.java
│   └── package-info.java
├── model
│   ├── CompilationReport.java
│   ├── DexArtifact.java
│   ├── DexOptions.java
│   ├── DexRequest.java
│   ├── FailureReport.java
│   ├── LogLevel.java
│   ├── MinSdkLevel.java
│   ├── SuccessReport.java
│   └── package-info.java
├── repository
│   ├── ArtifactRepository.java
│   ├── FileSystemArtifactRepository.java
│   ├── FileSystemInputRepository.java
│   ├── InputRepository.java
│   └── package-info.java
└── service
    ├── ArgumentTranslator.java
    ├── D8DexCompiler.java
    ├── DexCompiler.java
    ├── DiagnosticsCollector.java
    ├── RequestValidator.java
    └── package-info.java
```

The JAR also contains `META-INF/LICENSE` (MIT), `META-INF/NOTICE` and the
third-party licence of D8/R8 under `META-INF/licenses/r8/LICENSE`.

## Build

The build is a Gradle build using the
[shadow plugin](https://github.com/gradleup/shadow).

```bash
./gradlew clean build   # compiles, tests and builds every artifact
./gradlew test          # runs the tests
./gradlew jar           # builds build/libs/d8-wrapper.jar (thin)
./gradlew shadowJar     # builds build/libs/d8-wrapper-all.jar (self-contained)
./gradlew d8Jar         # copies the dexer to build/dexer/d8.jar
```

The build needs JDK 17 or newer; the toolchain is declared in
`build.gradle.kts`.  Archives are reproducible (stable order, no timestamps).

## Releases

The releases hold the artifacts as assets; the SHA-256 of every asset is in
the release notes:

```bash
gh release download v1.1.0 --repo mmunki/d8-wrapper --pattern 'd8-wrapper.jar'
sha256sum d8-wrapper.jar
```

## Licence

The wrapper is released under the MIT License; see `LICENSE`.

The D8 dexer that is shipped as `d8.jar` and inside `d8-wrapper-all.jar` is
part of the Android Open Source Project and is licensed under the Apache
License 2.0 and the BSD 3-Clause License.  Its complete licence text is in
`META-INF/licenses/r8/LICENSE`.
