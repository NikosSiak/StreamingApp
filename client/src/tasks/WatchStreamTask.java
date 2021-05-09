package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.SDPFileServer;
import constants.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class WatchStreamTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(WatchStreamTask.class);

  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;
  private String video;
  private String protocol;

  public WatchStreamTask(
    Socket socket,
    DataInputStream in,
    DataOutputStream out,
    String video,
    String protocol
  ) {
    this.socket = socket;
    this.in = in;
    this.out = out;
    this.video = video;
    if (protocol.equals("DEFAULT")) {
      this.protocol = WatchStreamTask.getDefaultProtocolForVideo(video);
    } else {
      this.protocol = protocol;
    }
  }

  @Override
  public void run() {
    try {
      out.writeUTF(this.video);
      out.writeUTF(this.protocol);

      int port = in.readInt();
      if (port == -1) {
        LOGGER.error("Server error: Cant play stream");
        return;
      }

      this.in.close();
      this.out.close();
      this.socket.close();
      
      if (protocol.equals("RTP")) {
        LOGGER.info("Getting sdp file from server");
        getSDPFile(video, port);
      }

      LOGGER.info("Starting stream");
      ProcessBuilder processBuilder = new ProcessBuilder(this.constructCommandLineArgs(protocol, port));
      processBuilder.start();
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }
  }

  private ArrayList<String> constructCommandLineArgs(String protocol, int port) {
    ArrayList<String> args = new ArrayList<>();

    args.add("ffplay");
    args.add("-loglevel");
    args.add("quiet");

    switch (protocol) {
      case "TCP": {
        String url = "tcp://" + Server.HOST + ":" + port;
        LOGGER.info("Stream url: {}", url);
        args.add(url);
        break;
      }
      case "UDP": {
        String url = "udp://" + Server.HOST+ ":" + port;
        LOGGER.info("Stream url: {}", url);
        args.add(url);
        break;
      }
      case "RTP": {
        args.add("-protocol_whitelist");
        args.add("file,rtp,udp");
        args.add("-i");
        args.add("video.sdp");
        break;
      }
      default: {
        LOGGER.warn("Invalid protocol: {}", protocol);
        throw new IllegalArgumentException("invalid protocol");
      }
    }

    return args;
  }

  private static String getDefaultProtocolForVideo(String video) {
    String resolution = WatchStreamTask.getResolution(video);

    switch (resolution) {
      case "240p":
        return "TCP";
      case "360p":
      case "480p":
        return "UDP";
      case "720p":
      case "1080p":
        return "RTP";
    }

    throw new IllegalArgumentException("unsupported resolution");
  }

  private static String getResolution(String video) {
    int index = video.lastIndexOf("-");
    if (index == -1) {
      throw new IllegalArgumentException("wrong file name format");
    }

    String[] metadata = video.substring(index + 1).split("\\.");
    if (metadata.length != 2) {
      throw new IllegalArgumentException("wrong file name format");
    }

    return metadata[0];
  }

  private void getSDPFile(String video, int port) {
    try (
      Socket socket = new Socket(SDPFileServer.HOST, SDPFileServer.PORT);
      DataInputStream in = new DataInputStream(socket.getInputStream());
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      FileOutputStream file = new FileOutputStream("video.sdp");
    ) {
      out.writeUTF(video);
      out.writeInt(port);

      int count;
      byte[] buffer = new byte[1024];
      while ((count = in.read(buffer)) > 0) {
        file.write(buffer, 0, count);
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }
  }
}
