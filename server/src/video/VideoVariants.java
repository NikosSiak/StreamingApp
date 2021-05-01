package video;

import java.util.ArrayList;

public class VideoVariants {
  private String videoName;
  private ArrayList<Variant> variants;
  private Resolution maxResolution;

  public VideoVariants(String videoName) {
    this.videoName = videoName;
    this.variants = new ArrayList<>();
    this.maxResolution = Resolution.RES_240;
  }
  
  public void addVariant(Format fileFormat, Resolution resolution) {
    this.variants.add(new Variant(fileFormat, resolution));
    if (maxResolution.isLessThan(resolution)) {
      maxResolution = resolution;
    }
  }

  public Variant[] getMissingVariants() {
    ArrayList<Variant> missingVariants = new ArrayList<>();
    
    for (Format format : Format.values()) {
      for (Resolution resolution : Resolution.values()) {
        if (resolution.isGreaterThan(this.maxResolution)) {
          break;
        }
        
        Variant variant = new Variant(format, resolution);
        if (!variants.contains(variant)) {
          missingVariants.add(variant);
        }
      }
    }

    return missingVariants.toArray(new Variant[missingVariants.size()]);
  }

  public String getVideoName() {
    return this.videoName;
  }
}
