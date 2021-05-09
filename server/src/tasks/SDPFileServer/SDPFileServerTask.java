package tasks.SDPFileServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.SDPFileServer;

public class SDPFileServerTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(SDPFileServerTask.class);

  @Override
  public void run() {
    ServerSocket serverSocket;
    try {
      serverSocket = new ServerSocket(SDPFileServer.PORT);
      serverSocket.setSoTimeout(1000);
    } catch (IOException e) {
      LOGGER.error("Failed to start sdp file server: {}", e.getMessage());
      return;
    }
    
    LOGGER.info("Listening for connections on port {}", SDPFileServer.PORT);

    Thread currentThread = Thread.currentThread();
    while (!currentThread.isInterrupted()) {
      try {
        Socket clientSocket = serverSocket.accept();
        Thread handleClientThread = new Thread(new HandleClientTask(clientSocket));
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
      LOGGER.error("Failed to close sdp server: {}", e.getMessage());
    }

    LOGGER.info("Stopped listening for connections on port {}", SDPFileServer.PORT);
  }
}
