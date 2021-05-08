package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
  private String serverIp;

  public WatchStreamTask(
    Socket socket,
    DataInputStream in,
    DataOutputStream out,
    String video,
    String protocol,
    String serverIp
  ) {
    this.socket = socket;
    this.in = in;
    this.out = out;
    this.video = video;
    this.protocol = protocol;
    this.serverIp = serverIp;
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
      case "TCP":
        args.add("tcp://" + this.serverIp + ":" + port);
        break;
      case "UDP":
        args.add("udp://" + this.serverIp + ":" + port);
        break;
      case "RTP":
        // TODO
        break;
      default:
        LOGGER.warn("Invalid protocol: {}", protocol);
        throw new IllegalArgumentException("invalid protocol");
    }

    return args;
  }
}
