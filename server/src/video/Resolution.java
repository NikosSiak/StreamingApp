package video;

public enum Resolution {
  RES_240(426, 240, "240p"),
  RES_360(640, 360, "360p"),
  RES_480(854, 480, "480p"),
  RES_720(1280, 720, "720p"),
  RES_1080(1920, 1080, "1080p");

  private int width;
  private int height;
  private String representation;

  private Resolution(int width, int height, String representation) {
    this.width = width;
    this.height = height;
    this.representation = representation;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public String getRepresentaion() {
    return this.representation;
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
        return Resolution.RES_720;
      case "1080p":
        return Resolution.RES_1080;
    }

    throw new IllegalArgumentException("Unsupported resolution");
  }

  public static Resolution getMaxResolutionForSpeed(float kbps) {
    if (kbps >= 4500) {
      return Resolution.RES_1080;
    } else if (kbps >= 2500) {
      return Resolution.RES_720;
    } else if (kbps >= 1000) {
      return Resolution.RES_480;
    } else if (kbps >= 750) {
      return Resolution.RES_360;
    }

    return Resolution.RES_240;
  }

  public boolean isLessThan(Resolution other) {
    return this.compareTo(other) < 0;
  }

  public boolean isGreaterThan(Resolution other) {
    return this.compareTo(other) > 0;
  }
}
