import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import video.Format;
import video.Resolution;
import video.Variant;
import video.VideoVariants;

public class StreamingServerService {
  private static final Logger LOGGER = LogManager.getLogger(StreamingServerService.class);
  private final File videosFolder;

  private final FFmpeg ffmpeg;
  private final FFprobe ffprobe;

  public StreamingServerService() throws IOException {
    this("videos");
  }

  public StreamingServerService(String videosFolderPath) throws IOException {
    this.videosFolder = new File(videosFolderPath);
    
    this.ffmpeg = new FFmpeg();
    this.ffprobe = new FFprobe();
  }


  private VideoVariants[] getVideoVariants() {
    HashMap<String, VideoVariants> fileVariants = new HashMap<>();

    for (String fileName : this.videosFolder.list()) {
      int index = fileName.lastIndexOf("-");
      if (index == -1) {
        LOGGER.warn("File: {} wrong file name format, file name format must be \"filename-resolution.format\"", fileName);
        continue;
      }

      String movieName = fileName.substring(0, index);

      String[] metadata = fileName.substring(index + 1).split("\\.");
      if (metadata.length != 2) {
        LOGGER.warn("File: {} wrong file name format, file name format must be \"filename-resolution.format\"", fileName);
        continue;
      }

      String resolution = metadata[0];
      String format = metadata[1];

      VideoVariants videoVariants = fileVariants.getOrDefault(movieName, new VideoVariants(movieName));
      
      try {
        Format videoFormat = Format.getEnum(format);
        Resolution videoResolution = Resolution.getEnum(resolution);
        videoVariants.addVariant(videoFormat, videoResolution);
      } catch (IllegalArgumentException e) {
        LOGGER.warn("{} for file \"{}\"", e.getMessage(), fileName);
        continue;
      }
      
      if (!fileVariants.containsKey(movieName)) {
        fileVariants.put(movieName, videoVariants);
      }
    }

    return fileVariants.values().toArray(new VideoVariants[fileVariants.size()]);
  }
}
