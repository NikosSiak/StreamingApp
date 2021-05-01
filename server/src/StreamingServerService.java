import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

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
  private final FFmpegExecutor executor;

  public StreamingServerService() throws IOException {
    this("videos");
  }

  public StreamingServerService(String videosFolderPath) throws IOException {
    this.videosFolder = new File(videosFolderPath);
    
    this.ffmpeg = new FFmpeg();
    this.ffprobe = new FFprobe();
    this.executor = new FFmpegExecutor(this.ffmpeg, this.ffprobe);
  }

  public void populateVideosFolder() {
    LOGGER.info("Creating video files...");
    
    VideoVariants[] videoVariants = getVideoVariants();

    for (VideoVariants video : videoVariants) {
      this.generateMissingVariantsForVideo(video.getFileName(), video.getMissingVariants());
    }

    LOGGER.info("Finished creating video files");
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
      
      try {
        Format videoFormat = Format.getEnum(format);
        Resolution videoResolution = Resolution.getEnum(resolution);

        VideoVariants videoVariants = fileVariants.getOrDefault(movieName, new VideoVariants(movieName, videoFormat));
        videoVariants.addVariant(videoFormat, videoResolution);

        if (!fileVariants.containsKey(movieName)) {
          fileVariants.put(movieName, videoVariants);
        }
      } catch (IllegalArgumentException e) {
        LOGGER.warn("{} for file \"{}\"", e.getMessage(), fileName);
        continue;
      }
    }

    return fileVariants.values().toArray(new VideoVariants[fileVariants.size()]);
  }

  private void generateMissingVariantsForVideo(String filename, Variant[] variants) {
    String inputPath = new File(this.videosFolder, filename).getAbsolutePath();
    
    for (Variant variant : variants) {
      try {
        LOGGER.info("Creating {}", variant.getOutputPath(inputPath));
        FFmpegBuilder builder = new FFmpegBuilder()
          .setInput(inputPath)
          .addOutput(variant.getOutputPath(inputPath))
          .setVideoResolution(variant.getResolution().getWidth(), variant.getResolution().getHeight())
          .done();

        this.executor.createJob(builder).run();
      } catch (Exception e) {
        LOGGER.error("{}, {}", e.getMessage(), variant.getOutputPath(inputPath));
      }
      
    }
  }
}
