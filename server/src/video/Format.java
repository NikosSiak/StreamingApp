package video;

public enum Format {
  AVI(".avi"),
  MP4(".mp4"),
  MKV(".mkv");

  private String extension;

  private Format(String extension) {
    this.extension = extension;
  }

  public String getExtension() {
    return this.extension;
  }

  public static Format getEnum(String value) throws IllegalArgumentException {
    value = value.toUpperCase();
    
    switch (value) {
      case "AVI":
        return Format.AVI;
      case "MP4":
        return Format.MP4;
      case "MKV":
        return Format.MKV;
    }

    throw new IllegalArgumentException("Unsupported format");
  }
}
