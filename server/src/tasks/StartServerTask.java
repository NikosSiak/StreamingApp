package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
    GenerateVideosTask generateVideosTask = new GenerateVideosTask(this.videosFolder, this.ffmpegExecutor);
    generateVideosTask.run();

    ServerSocket serverSocket;
    try  {
      serverSocket = new ServerSocket(1312);
    } catch (IOException e) {
      LOGGER.error("Failed to start server: {}", e.getMessage());
      return;
    }
    
    Thread currentThread = Thread.currentThread();
    while (!currentThread.isInterrupted()) {
      try {
        Socket clientSocket = serverSocket.accept();
        Thread handleClientThread = new Thread(new HandleClientTask(clientSocket));
        handleClientThread.start();
      } catch (IOException e) {
        LOGGER.error(e.getMessage());
      }
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      LOGGER.error("Failed to close server: {}", e.getMessage());
    }
  }
}
