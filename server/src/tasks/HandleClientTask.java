package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.Server;
import video.Format;
import video.Resolution;
import video.VideoFile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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
    ArrayList<String> commandLineArguments = null;
    
    String clientAddress = clientSocket.getInetAddress().toString();
    int port = this.clientSocket.getPort();

    try (
      DataInputStream in = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
      DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
    ) {

      LOGGER.info("Client {} connected", clientAddress);
      
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

      String videoToStream = in.readUTF();
      String protocol = in.readUTF().toUpperCase();

      try {
        commandLineArguments = this.constructCommandLineArgs(videoToStream, protocol, port);
      } catch (IllegalArgumentException e) {
        LOGGER.error(e.getMessage());
        port = -1;
      }

      // send the port number that the video will be streamed
      out.writeInt(port);

    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
    }

    try {
      this.clientSocket.close();
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      return;
    }

    if (commandLineArguments == null) {
      LOGGER.warn("Stream wont start for client: {}", clientAddress);
      return;
    }

    LOGGER.info("Starting stream for client: {}, on port: {}", clientAddress, port);
    ProcessBuilder processBuilder = new ProcessBuilder(commandLineArguments);
    try {
      processBuilder.start();
    } catch (IOException e) {
      LOGGER.error("Failed to start stream for client: {}, reason: {}", clientAddress, e.getMessage());
    }
  }

  public ArrayList<String> constructCommandLineArgs(String videoName, String protocol, int port) {
    ArrayList<String> args = new ArrayList<>();
    String videoPath = new File(this.videosFolder, videoName).getAbsolutePath();

    args.add("ffmpeg");
    args.add("-loglevel");
    args.add("quiet");

    if (protocol.equals("UDP")) {
      args.add("-re");
      args.add("-i");
      args.add(videoPath);
      args.add("-f");
      args.add("mpegts");
      args.add("udp://" + Server.HOST + ":" + port);
    } else if (protocol.equals("TCP")) {
      args.add("-i");
      args.add(videoPath);
      args.add("-f");
      args.add("mpegts");
      args.add("tcp://" + Server.HOST + ":" + port + "?listen");
    } else if (protocol.equals("RTP")) {
      // TODO
    } else {
      throw new IllegalArgumentException("invalid protocol");
    }

    return args;
  }
}
