package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import net.bramp.ffmpeg.FFmpegExecutor;

public class StartServerTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(StartServerTask.class);

  private final File videosFolder;
  private final FFmpegExecutor ffmpegExecutor;

  public StartServerTask(File videosFolder, FFmpegExecutor ffmpegExecutor) {
    this.videosFolder = videosFolder;
    this.ffmpegExecutor = ffmpegExecutor;
  }

  @Override
  public void run() {
    Thread thread = new Thread(new GenerateVideosTask(this.videosFolder, this.ffmpegExecutor));
    thread.setDaemon(true);
    thread.start();
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
