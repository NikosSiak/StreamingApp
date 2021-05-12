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

  public String getOutputPath(String inputPath) {
    int index = inputPath.lastIndexOf("-");
    if (index == -1) {
      throw new IllegalArgumentException("Invalid file path");
    }

    StringBuilder builder = new StringBuilder(inputPath.substring(0, index));
    builder.append("-");
    builder.append(this.resolution.getRepresentation());
    builder.append(this.format.getExtension());
    
    return builder.toString();
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
