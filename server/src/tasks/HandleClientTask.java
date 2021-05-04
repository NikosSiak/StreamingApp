package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import video.Format;
import video.Resolution;
import video.VideoFile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.function.Predicate;

public class HandleClientTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(HandleClientTask.class);
  private Socket clientSocket;
  private File videosFolder;

  public HandleClientTask(Socket clientSocket, File videosFolder) {
    this.clientSocket = clientSocket;
    this.videosFolder = videosFolder;
  }

  @Override
  public void run() {
    try (
      DataInputStream in = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
      DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
    ) {

      LOGGER.info("Client {} connected", clientSocket.getInetAddress());
      
      float connectionSpeed = in.readFloat();
      Format format = Format.getEnum(in.readUTF());

      LOGGER.info("Connection speed: {}", connectionSpeed);
      LOGGER.info("Format: {}", format);

      VideoFile[] videos = VideoFile.getVideoFilesInFolder(this.videosFolder);
      Resolution maxResolution = Resolution.getMaxResolutionForSpeed(connectionSpeed);

      Predicate<VideoFile> filterFiles = 
        (video) -> video.getFormat() == format && !video.getResolution().isGreaterThan(maxResolution);
      videos = Arrays.stream(videos).filter(filterFiles).toArray(VideoFile[]::new);

      for (VideoFile video : videos) {
        out.writeUTF(video.getFileName());
      }

      out.writeUTF("#END#");

    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
    }

    try {
      this.clientSocket.close();
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }
  }
}
