package tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HandleClientTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(HandleClientTask.class);
  private Socket clientSocket;

  public HandleClientTask(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try {
      InputStream socketIn = this.clientSocket.getInputStream();
      OutputStream socketOut = this.clientSocket.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
