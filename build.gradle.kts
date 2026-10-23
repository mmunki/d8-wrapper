import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  application
  alias(libs.plugins.shadow)
}

group = "tw.mmunki"
version = "1.1.0"

description = "A production-ready wrapper around the D8 dexer"

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
  mainClass = "tw.mmunki.d8wrapper.cli.Main"
}

// The dexer is shipped as a separate d8.jar next to the thin wrapper, and it
// is the only dependency that is copied into build/dexer.
val d8 = configurations.create("d8") {
  isCanBeConsumed = false
  isCanBeResolved = true
}

dependencies {
  add(d8.name, libs.r8)
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

// Reproducible archives: the same sources and dependencies always produce the
// same bytes, which is what makes a published checksum meaningful.
tasks.withType<AbstractArchiveTask>().configureEach {
  isPreserveFileTimestamps = false
  isReproducibleFileOrder = true
}

// d8-wrapper.jar is the wrapper only.  The dexer is deliberately not inside
// it: it is published as d8.jar and is found through the Class-Path entry of
// the manifest.  d8-wrapper-all.jar is the self-contained alternative.
tasks.named<Jar>("jar") {
  archiveBaseName.set("d8-wrapper")
  archiveVersion.set("")
  archiveClassifier.set("")
  archiveFileName.set("d8-wrapper.jar")
  manifest {
    attributes(
        "Main-Class" to application.mainClass.get(),
        "Class-Path" to "d8.jar",
        "Implementation-Title" to "d8-wrapper",
        "Implementation-Version" to project.version,
        "Implementation-Vendor" to "mmunki",
        "Multi-Release" to "false",
    )
  }
}

tasks.named<ShadowJar>("shadowJar") {
  archiveBaseName.set("d8-wrapper")
  archiveVersion.set("")
  archiveClassifier.set("all")
  archiveFileName.set("d8-wrapper-all.jar")
  mergeServiceFiles()
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/versions/**")
  exclude("module-info.class", "META-INF/INDEX.LIST")
  // Keep the project licence and the third-party licence separate.
  exclude("LICENSE", "LICENSE.txt", "NOTICE", "NOTICE.txt")
}

// The dexer that belongs next to d8-wrapper.jar.  It is published as a
// separate release asset, never merged into the wrapper JAR.
tasks.register<Copy>("d8Jar") {
  from(d8.filter { it.name.startsWith("r8-") })
  into(layout.buildDirectory.dir("dexer"))
  rename { "d8.jar" }
}

tasks.named("assemble") {
  dependsOn(tasks.named("shadowJar"), tasks.named("d8Jar"))
}
