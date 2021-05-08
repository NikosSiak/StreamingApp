package tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetVideosTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(GetVideosTask.class);

  private DataInputStream in;
  private DataOutputStream out;
  private String format;
  private float connectionSpeed;
  private Consumer<String[]> callback;

  public GetVideosTask(
    DataInputStream in,
    DataOutputStream out,
    String format,
    float connectionSpeed,
    Consumer<String[]> callback
  ) {
    this.in = in;
    this.out = out;
    this.format = format;
    this.connectionSpeed = connectionSpeed;
    this.callback = callback;
  }

  @Override
  public void run() {
    ArrayList<String> videos = new ArrayList<>();

    try {
      this.out.writeFloat(this.connectionSpeed);
      this.out.writeUTF(this.format);

      boolean stop;
      do {
        stop = true;
        String videoFile =  in.readUTF();
        if (!videoFile.equals("#END#")) {
          videos.add(videoFile);
          stop = false;
        }
      } while (!stop);

    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }

    this.callback.accept(videos.toArray(new String[videos.size()]));
  }
}
