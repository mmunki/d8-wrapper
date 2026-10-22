pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    // The D8 dexer (com.android.tools:r8) is published on the Google Maven
    // repository only.
    google()
    mavenCentral()
  }
}

rootProject.name = "d8-wrapper"
