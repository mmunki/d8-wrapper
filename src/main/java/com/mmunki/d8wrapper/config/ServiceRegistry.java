package com.mmunki.d8wrapper.config;

import com.mmunki.d8wrapper.repository.ArtifactRepository;
import com.mmunki.d8wrapper.repository.FileSystemArtifactRepository;
import com.mmunki.d8wrapper.repository.FileSystemInputRepository;
import com.mmunki.d8wrapper.repository.InputRepository;
import com.mmunki.d8wrapper.service.ArgumentTranslator;
import com.mmunki.d8wrapper.service.D8DexCompiler;
import com.mmunki.d8wrapper.service.DexCompiler;
import com.mmunki.d8wrapper.service.RequestValidator;

/**
 * Builds the object graph of the wrapper.
 *
 * <p>The registry is the dependency injection of the application. Everything is created here and
 * nowhere else, the parts of the application therefore know nothing about how they are wired
 * together.
 */
public final class ServiceRegistry {

  private final WrapperConfig config;
  private final ArgumentTranslator argumentTranslator;
  private final RequestValidator requestValidator;
  private final ArtifactRepository artifactRepository;
  private final InputRepository inputRepository;

  /** Creates the registry with the default configuration. */
  public ServiceRegistry() {
    this(new SystemPropertyConfigLoader().load());
  }

  /**
   * Creates the registry.
   *
   * @param config the configuration of the wrapper
   */
  public ServiceRegistry(WrapperConfig config) {
    this.config = config;
    this.argumentTranslator = new ArgumentTranslator();
    this.inputRepository = new FileSystemInputRepository();
    this.artifactRepository = new FileSystemArtifactRepository();
    this.requestValidator = new RequestValidator(inputRepository);
  }

  /**
   * Returns the configuration of the wrapper.
   *
   * @return the configuration
   */
  public WrapperConfig config() {
    return config;
  }

  /**
   * Creates the compiler of the wrapper.
   *
   * @return the compiler
   */
  public DexCompiler compiler() {
    return new D8DexCompiler(
        argumentTranslator,
        requestValidator,
        artifactRepository,
        inputRepository,
        config.logLevel());
  }
}
