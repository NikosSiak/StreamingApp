package tasks;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import video.Format;
import video.Resolution;
import video.Variant;
import video.VideoFile;
import video.VideoVariants;

public class GenerateVideosTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(GenerateVideosTask.class);
  private final File videosFolder;
  private final FFmpegExecutor ffmpegExecutor;

  public GenerateVideosTask(File videosFolder, FFmpegExecutor ffmpegExecutor) {
    this.videosFolder = videosFolder;
    this.ffmpegExecutor = ffmpegExecutor;
  }

  @Override
  public void run() {
    LOGGER.info("Creating video files...");
    
    VideoVariants[] videoVariants = getVideoVariants();

    for (VideoVariants video : videoVariants) {
      this.generateMissingVariantsForVideo(video.getMaxExistingResolutionFileName(), video.getMissingVariants());
    }

    LOGGER.info("Finished creating video files");
  }

  private VideoVariants[] getVideoVariants() {
    HashMap<String, VideoVariants> fileVariants = new HashMap<>();

    for (String fileName : this.videosFolder.list()) {
      try {
        VideoFile videoFile = new VideoFile(this.videosFolder, fileName);
        String videoName = videoFile.getName();
        Format videoFormat = videoFile.getFormat();
        Resolution videoResolution = videoFile.getResolution();

        VideoVariants videoVariants = fileVariants.getOrDefault(videoName, new VideoVariants(videoName, videoFormat));
        videoVariants.addVariant(videoFormat, videoResolution);

        if (!fileVariants.containsKey(videoName)) {
          fileVariants.put(videoName, videoVariants);
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
      String outputPath = variant.getOutputPath(inputPath);
      try {
        LOGGER.info("Creating {}", outputPath);
        FFmpegBuilder builder = new FFmpegBuilder()
          .setInput(inputPath)
          .addOutput(outputPath)
          .setVideoResolution(variant.getResolution().getWidth(), variant.getResolution().getHeight())
          .done();

        // Thread thread = new Thread(this.ffmpegExecutor.createJob(builder));
        // thread.setDaemon(true);
        // thread.start();
        this.ffmpegExecutor.createJob(builder).run();
      } catch (Exception e) {
        LOGGER.error("{}, {}", e.getMessage(), outputPath);
      }
    }
  }

}
