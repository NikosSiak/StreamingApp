package video;

public class Variant {
  private Format format;
  private Resolution resolution;

  public Variant(Format format, Resolution resolution) {
    this.format = format;
    this.resolution = resolution;
  }

  public Format getFormat() {
    return this.format;
  }

  public Resolution getResolution() {
    return this.resolution;
  }
}
