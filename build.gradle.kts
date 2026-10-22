import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  application
  alias(libs.plugins.shadow)
}

group = "com.mmunki"
version = "1.0.0"

description = "A production ready wrapper around the D8 dexer"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
  withSourcesJar()
}

dependencies {
  implementation(libs.r8)

  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.platform.launcher)
}

application {
  mainClass = "com.mmunki.d8wrapper.cli.Main"
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
  options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.withType<Javadoc>().configureEach {
  options.encoding = "UTF-8"
  (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}

// The deliverable is a single runnable JAR called d8-wrapper.jar.  It holds
// the wrapper classes and the D8 dexer, nothing else.
tasks.named<ShadowJar>("shadowJar") {
  archiveBaseName = "d8-wrapper"
  archiveClassifier = ""
  archiveVersion = ""
  mergeServiceFiles()
  manifest {
    attributes(
      "Main-Class" to "com.mmunki.d8wrapper.cli.Main",
      "Implementation-Title" to "d8-wrapper",
      "Implementation-Version" to project.version,
      "Multi-Release" to "false",
    )
  }
  exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/versions/**")
  exclude("module-info.class", "META-INF/INDEX.LIST")
}

// The plain JAR is of no use for the user, the fat JAR is the artifact.
tasks.jar {
  archiveClassifier = "thin"
}

tasks.build {
  dependsOn(tasks.named("shadowJar"))
}
