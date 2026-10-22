# d8-wrapper

A production ready wrapper around the **D8 dexer**, the dexer of the Android build tools.

The wrapper turns the command line of the dexer into a small, validated and stable interface,
collects the DEX files that were written and reports the checksum of every one of them. The
result is a single runnable JAR that holds the wrapper and the dexer, there is nothing to
install and nothing to download besides the JAR itself.

```
java -jar d8-wrapper.jar --min-api 21 --output out build/classes
```

## What it does

- validates the command line before anything is compiled, every message names the option that
  caused it
- compiles class files, JAR files and archives into DEX files
- collects the DEX files of the output directory and computes the SHA-256 checksum of each one
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
  --main-dex-rules <file>    the ProGuard rules that select the classes of the primary DEX
                             file
  --main-dex-list-output <file>
                             the file the resulting main dex list is written to
  --pg-map <file>            the mapping file of a distribution build
  --startup-profile <file>   the ART profile that shapes the DEX files
  --quiet                    print the errors only
  --verbose                  print everything, including the diagnostics
  --help                     print this text
  --version                  print the versions of the wrapper and the dexer
```

An option is written as `--name` or `--name=value`, an option that needs a value can also be
written as `--name value`. Everything after `--` is an input.

### Exit codes

| Code | Meaning |
| --- | --- |
| 0 | the DEX files were written |
| 2 | the command line could not be understood |
| 3 | the request cannot succeed |
| 4 | the dexer refused to compile the inputs |
| 70 | the wrapper hit a failure it does not know about |

## The architecture

The wrapper is layered, every layer only knows the layer below it.

| Package | What it holds |
| --- | --- |
| `com.mmunki.d8wrapper.cli` | the entry point, the parser of the command line and the usage |
| `com.mmunki.d8wrapper.config` | the configuration and the dependency injection |
| `com.mmunki.d8wrapper.model` | the immutable data classes, the records and the enums |
| `com.mmunki.d8wrapper.repository` | the access to the inputs and to the artifacts |
| `com.mmunki.d8wrapper.service` | the business logic, the compiler and the validator |
| `com.mmunki.d8wrapper.exception` | the exceptions of the wrapper |

The code follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html),
uses Java 17 and keeps every class small and focused. The interfaces are the boundaries, the
compiler is programmed against the interface of the compiler and not against the dexer.

```
src/main/java/com/mmunki/d8wrapper
├── cli
│   ├── CliArguments.java
│   ├── CommandLineParser.java
│   ├── ExitCode.java
│   ├── Main.java
│   ├── RequestBuilder.java
│   └── UsagePrinter.java
├── config
│   ├── ConfigLoader.java
│   ├── ServiceRegistry.java
│   ├── SystemPropertyConfigLoader.java
│   └── WrapperConfig.java
├── exception
│   ├── CompilationFailedException.java
│   ├── InputValidationException.java
│   ├── InvalidOptionException.java
│   └── WrapperException.java
├── model
│   ├── CompilationReport.java
│   ├── DexArtifact.java
│   ├── DexOptions.java
│   ├── DexRequest.java
│   ├── FailureReport.java
│   ├── LogLevel.java
│   ├── MinSdkLevel.java
│   └── SuccessReport.java
├── repository
│   ├── ArtifactRepository.java
│   ├── FileSystemArtifactRepository.java
│   ├── FileSystemInputRepository.java
│   └── InputRepository.java
└── service
    ├── ArgumentTranslator.java
    ├── D8DexCompiler.java
    ├── DexCompiler.java
    ├── DiagnosticsCollector.java
    └── RequestValidator.java
```

## The build

The build is a Gradle build, it produces the fat JAR with the
[shadow plugin](https://github.com/gradleup/shadow).

```bash
./gradlew clean build          # compiles, tests and builds the JAR
./gradlew shadowJar            # builds build/libs/d8-wrapper.jar
./gradlew test                 # runs the tests
```

The JAR holds the classes of the wrapper and the dexer, it is the only file that has to be
copied around. The dexer is the `com.android.tools:r8` artifact, it comes from the Google
Maven repository.

The build needs a JDK 17 or newer, the toolchain is declared in `build.gradle.kts`.

## The releases

The releases of this repository hold the JAR as an asset, the SHA-256 of the asset is in the
notes of the release:

```bash
gh release download v1.0.0 --repo mmunki/d8-wrapper --pattern 'd8-wrapper.jar'
sha256sum d8-wrapper.jar
```

## The licence

The wrapper is under the Apache License 2.0, the dexer that it carries is under the
Apache License 2.0 and the BSD 3-Clause License, both of Google.
