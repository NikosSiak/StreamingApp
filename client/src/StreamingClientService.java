import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.Server;
import fr.bmartel.speedtest.SpeedTestSocket;
import listeners.SpeedTestListener;
import tasks.GetVideosTask;
import tasks.WatchStreamTask;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class StreamingClientService {

  private static final Logger LOGGER = LogManager.getLogger(StreamingClientService.class);

  private Socket socket;
  private DataInputStream socketIn;
  private DataOutputStream socketOut;
  private SpeedTestListener speedTestListener;

  public void startSpeedTest(DoubleConsumer onProgressCallback, Runnable onCompletionCallback) {
    this.speedTestListener = new SpeedTestListener(onProgressCallback, onCompletionCallback);
    SpeedTestSocket speedTestSocket = new SpeedTestSocket();
    speedTestSocket.addSpeedTestListener(this.speedTestListener);
    speedTestSocket.setDownloadSetupTime(5000);
    speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/100M.iso");
  }

  public void getVideos(String format, Consumer<String[]> callback) {
    if (this.speedTestListener == null) {
      LOGGER.warn("Speed test did not run first, running one now");
      startSpeedTest(percent -> {}, () -> getVideos(format, callback));
      return;
    }

    float connectionSpeed = speedTestListener.getTransferRateBit().floatValue() / 1000; // convert to kbps
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
