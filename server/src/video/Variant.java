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

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj.getClass() != this.getClass()) {
      return false;
    }

    final Variant other = (Variant) obj;
    
    if (this.format != other.format) {
      return false;
    }

    return this.resolution == other.resolution;
  }
}
