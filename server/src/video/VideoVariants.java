package video;

import java.util.ArrayList;

public class VideoVariants {
  private String videoName;
  private ArrayList<Variant> variants;

  public VideoVariants(String videoName) {
    this.videoName = videoName;
    this.variants = new ArrayList<>();
  }
  
  public void addVariant(Format fileFormat, Resolution resolution) {
    this.variants.add(new Variant(fileFormat, resolution));
  }

  public Variant[] getMissingVariants() {
    return new Variant[0];
  }

  public String getVideoName() {
    return this.videoName;
  }
}
