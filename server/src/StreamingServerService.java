import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import tasks.StartServerTask;
import tasks.SDPFileServer.SDPFileServerTask;

import java.io.File;
import java.io.IOException;

public class StreamingServerService {
  private static final Logger LOGGER = LogManager.getLogger(StreamingServerService.class);
  private final File videosFolder;

  private final FFmpeg ffmpeg;
  private final FFprobe ffprobe;
  private final FFmpegExecutor executor;

  private Thread runningServerThread;
  private Thread runningSDPServerThread;

  public StreamingServerService() throws IOException {
    this("videos");
  }

  public StreamingServerService(String videosFolderPath) throws IOException {
    this.videosFolder = new File(videosFolderPath);
    
    this.ffmpeg = new FFmpeg();
    this.ffprobe = new FFprobe();
    this.executor = new FFmpegExecutor(this.ffmpeg, this.ffprobe);
  }

  public void startServer(Runnable serverStarted) {
    try {
      this.runningServerThread = new Thread(new StartServerTask(this.videosFolder, this.executor, serverStarted));
      this.runningServerThread.setDaemon(true);
      this.runningServerThread.start();

      this.runningSDPServerThread = new Thread(new SDPFileServerTask());
      this.runningSDPServerThread.setDaemon(true);
      this.runningSDPServerThread.start();
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
  }

  public void stopServer() {
    if (this.runningServerThread != null) {
      this.runningServerThread.interrupt();
    }

    if (this.runningSDPServerThread != null) {
      this.runningSDPServerThread.interrupt();
    }
  }
}
