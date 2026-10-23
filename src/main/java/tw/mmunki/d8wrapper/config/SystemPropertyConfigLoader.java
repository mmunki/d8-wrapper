package tw.mmunki.d8wrapper.config;

import tw.mmunki.d8wrapper.model.LogLevel;

/**
 * The configuration loader that reads the system properties of the JVM.
 *
 * <p>The loader never fails, a property that holds a value the loader does not understand is
 * ignored and the default is used instead.
 */
public final class SystemPropertyConfigLoader implements ConfigLoader {

  /** The property that holds the level of the output. */
  public static final String PROPERTY_LOG_LEVEL = "d8wrapper.log.level";

  /** The property that turns a warning into a failure. */
  public static final String PROPERTY_FAIL_ON_WARNING = "d8wrapper.fail.on.warning";

  @Override
  public WrapperConfig load() {
    LogLevel level = LogLevel.DEFAULT;
    String configuredLevel = System.getProperty(PROPERTY_LOG_LEVEL);
    if (configuredLevel != null && !configuredLevel.isBlank()) {
      try {
        level = LogLevel.fromLabel(configuredLevel.trim());
      } catch (IllegalArgumentException e) {
        level = LogLevel.DEFAULT;
      }
    }
    return new WrapperConfig(
        level,
        Boolean.parseBoolean(
            System.getProperty(PROPERTY_FAIL_ON_WARNING, Boolean.FALSE.toString())));
  }
}
