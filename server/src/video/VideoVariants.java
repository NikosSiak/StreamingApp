package video;

import java.util.ArrayList;

public class VideoVariants {
  private String videoName;
  private ArrayList<Variant> variants;
  private Resolution maxExistingResolution;
  private Format maxExistingResolutionFileFormat;

  public VideoVariants(String videoName, Format format) {
    this.videoName = videoName;
    this.variants = new ArrayList<>();
    this.maxExistingResolution = Resolution.RES_240;
    this.maxExistingResolutionFileFormat = format;
  }
  
  public void addVariant(Format fileFormat, Resolution resolution) {
    this.variants.add(new Variant(fileFormat, resolution));
    if (maxExistingResolution.isLessThan(resolution)) {
      maxExistingResolution = resolution;
      maxExistingResolutionFileFormat = fileFormat;
    }
  }

  public Variant[] getMissingVariants() {
    ArrayList<Variant> missingVariants = new ArrayList<>();
    
    for (Format format : Format.values()) {
      for (Resolution resolution : Resolution.values()) {
        if (resolution.isGreaterThan(this.maxExistingResolution)) {
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

  public String getFileName() {
    StringBuilder builder = new StringBuilder(this.videoName);
    builder.append("-");
    builder.append(this.maxExistingResolution.getRepresentation());
    builder.append(this.maxExistingResolutionFileFormat.getExtension());

    return builder.toString();
  }
}
