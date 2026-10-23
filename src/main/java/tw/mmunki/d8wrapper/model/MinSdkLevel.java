package tw.mmunki.d8wrapper.model;

/**
 * The Android API levels that the D8 dexer knows about.
 *
 * <p>D8 accepts every level between {@link #MIN_LEVEL} and {@link #MAX_LEVEL}, the enum only
 * carries the name of the platform release for the levels that have one.
 */
public enum MinSdkLevel {
  LEVEL_21(21, "Lollipop"),
  LEVEL_22(22, "Lollipop MR1"),
  LEVEL_23(23, "Marshmallow"),
  LEVEL_24(24, "Nougat"),
  LEVEL_25(25, "Nougat MR1"),
  LEVEL_26(26, "Oreo"),
  LEVEL_27(27, "Oreo MR1"),
  LEVEL_28(28, "Pie"),
  LEVEL_29(29, "Q"),
  LEVEL_30(30, "R"),
  LEVEL_31(31, "S"),
  LEVEL_32(32, "S V2"),
  LEVEL_33(33, "Tiramisu"),
  LEVEL_34(34, "Upside Down Cake"),
  LEVEL_35(35, "Vanilla Ice Cream");

  /** The lowest API level that D8 accepts. */
  public static final int MIN_LEVEL = 1;

  /** The highest API level that this wrapper supports. */
  public static final int MAX_LEVEL = 35;

  private final int level;
  private final String platformName;

  MinSdkLevel(int level, String platformName) {
    this.level = level;
    this.platformName = platformName;
  }

  /**
   * Returns the numeric API level.
   *
   * @return the API level
   */
  public int level() {
    return level;
  }

  /**
   * Returns the name of the Android platform release of the level.
   *
   * @return the platform name
   */
  public String platformName() {
    return platformName;
  }

  /**
   * Returns the platform name of an API level, when the level has one.
   *
   * @param level the numeric API level
   * @return the platform name or {@code null} when the level is unknown
   */
  public static String platformNameOf(int level) {
    for (MinSdkLevel candidate : values()) {
      if (candidate.level == level) {
        return candidate.platformName;
      }
    }
    return null;
  }

  /**
   * Tells whether the given API level is inside the range that the wrapper supports.
   *
   * @param level the numeric API level
   * @return {@code true} when the level is supported
   */
  public static boolean isSupported(int level) {
    return level >= MIN_LEVEL && level <= MAX_LEVEL;
  }
}
