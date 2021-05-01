package video;

public enum Resolution {
  RES_240(426, 240),
  RES_360(640, 360),
  RES_480(865, 480),
  RES_720(1280, 720),
  RES_1080(1920, 1080);

  private int width;
  private int height;

  private Resolution(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public static Resolution getEnum(String value) throws IllegalArgumentException {
    switch (value) {
      case "240p":
        return Resolution.RES_240;
      case "360p":
        return Resolution.RES_360;
      case "480p":
        return Resolution.RES_480;
      case "720p":
        return Resolution.RES_480;
      case "1080p":
        return Resolution.RES_1080;
    }

    throw new IllegalArgumentException("Unsupported resolution");
  }

  public boolean isLessThan(Resolution other) {
    return this.compareTo(other) < 0;
  }

  public boolean isGreaterThan(Resolution other) {
    return this.compareTo(other) > 0;
  }
}
