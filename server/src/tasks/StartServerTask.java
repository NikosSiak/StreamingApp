package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.Server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import net.bramp.ffmpeg.FFmpegExecutor;

public class StartServerTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(StartServerTask.class);

  private final File videosFolder;
  private final FFmpegExecutor ffmpegExecutor;
  private final Runnable serverStarted;

  public StartServerTask(File videosFolder, FFmpegExecutor ffmpegExecutor, Runnable serverStarted) {
    this.videosFolder = videosFolder;
    this.ffmpegExecutor = ffmpegExecutor;
    this.serverStarted = serverStarted;
  }

  @Override
  public void run() {
    GenerateVideosTask generateVideosTask = new GenerateVideosTask(this.videosFolder, this.ffmpegExecutor);
    generateVideosTask.run();

    this.serverStarted.run();

    LOGGER.info("Listening for connections on port {}", Server.PORT);

    ServerSocket serverSocket;
    try  {
      serverSocket = new ServerSocket(Server.PORT);
      serverSocket.setSoTimeout(1000);
    } catch (IOException e) {
      LOGGER.error("Failed to start server: {}", e.getMessage());
      return;
    }
    
    Thread currentThread = Thread.currentThread();
    while (!currentThread.isInterrupted()) {
      try {
        Socket clientSocket = serverSocket.accept();
        Thread handleClientThread = new Thread(new HandleClientTask(clientSocket, this.videosFolder));
        handleClientThread.start();
      } catch (SocketTimeoutException e) {
        // blank
      } catch (IOException e) {
        LOGGER.error(e.getMessage());
      }
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      LOGGER.error("Failed to close server: {}", e.getMessage());
    }

    LOGGER.info("Stopped listening for connections on port {}", Server.PORT);
  }
}
