package tasks.SDPFileServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class HandleClientTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(HandleClientTask.class);
  private Socket clientSocket;

  public HandleClientTask(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    String clientAddress = this.clientSocket.getInetAddress().toString();

    try (
      DataInputStream in = new DataInputStream(this.clientSocket.getInputStream());
      DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
    ) {
      
      LOGGER.info("Client {} connected", clientAddress);

      String videoName = in.readUTF();
      int port = in.readInt();

      Thread.sleep(1000);

      String filename = "sdpFiles/" + videoName + "_" + port + ".sdp";
      LOGGER.info("Sending file: {}, to client: {}", filename, clientAddress);
      FileInputStream sdpFile = new FileInputStream(filename);
      byte[] buffer = new byte[1024];
      int count;
      while ((count = sdpFile.read(buffer)) > 0) {
        System.out.println(buffer);
        out.write(buffer, 0, count);
      }

      sdpFile.close();

    } catch (FileNotFoundException e) {
      LOGGER.error(e.getMessage());
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
