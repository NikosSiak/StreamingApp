import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.Server;
import tasks.GetVideosTask;
import tasks.WatchStreamTask;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class StreamingClientService {

  private static final Logger LOGGER = LogManager.getLogger(StreamingClientService.class);

  private Socket socket;
  private DataInputStream socketIn;
  private DataOutputStream socketOut;

  public void getVideos(String format, float connectionSpeed, Consumer<String[]> callback) {
    try {
      this.socket = new Socket(Server.HOST, Server.PORT);
      this.socketIn = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
      this.socketOut = new DataOutputStream(this.socket.getOutputStream());

      Thread thread = new Thread(new GetVideosTask(this.socketIn, this.socketOut, format, connectionSpeed, callback));
      thread.setDaemon(true);
      thread.start();
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }
  }

  public void watchStream(String video, String protocol) {
    Thread thread = new Thread(new WatchStreamTask(this.socket, this.socketIn, this.socketOut, video, protocol));
    thread.setDaemon(true);
    thread.start();
  }
}
