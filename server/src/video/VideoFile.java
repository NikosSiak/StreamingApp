package video;

import java.io.File;
import java.util.ArrayList;

public class VideoFile {
  private String path;
  private String name;
  private Resolution resolution;
  private Format format;

  public VideoFile(File folder, String fileName) {
    this.path = new File(folder, fileName).getAbsolutePath();

    int index = fileName.lastIndexOf("-");
    if (index == -1) {
      throw new IllegalArgumentException("wrong file name format");
    }

    this.name = fileName.substring(0, index);

    String[] metadata = fileName.substring(index + 1).split("\\.");
    if (metadata.length != 2) {
      throw new IllegalArgumentException("wrong file name format");
    }

    this.resolution = Resolution.getEnum(metadata[0]);
    this.format = Format.getEnum(metadata[1]);
  }

  public String getPath() {
    return this.path;
  }

  public String getName() {
    return this.name;
  }

  public Resolution getResolution() {
    return this.resolution;
  }

  public Format getFormat() {
    return this.format;
  }

  public static VideoFile[] getVideoFilesInFolder(File folder) {
    ArrayList<VideoFile> files = new ArrayList<>();

    for (String fileName : folder.list()) {
      try {
        files.add(new VideoFile(folder, fileName));
      } catch (IllegalArgumentException e) {
        // blank
      }
    }

    return files.toArray(new VideoFile[files.size()]);
  }
}
