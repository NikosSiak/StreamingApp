import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StreamingServerController {
  private static final Logger LOGGER = LogManager.getLogger(StreamingServerController.class);
  private final StreamingServerService service;
  
  @FXML
  private Button startServerButton;
  @FXML
  private Button stopServerButton;

  public StreamingServerController(StreamingServerService service) {
    this.service = service;
  }

  public void startServer(ActionEvent event) {
    LOGGER.info("starting server");

    toggleButtons();
    try {
      this.service.populateVideosFolder();
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("{}", e.getMessage());
    }
  }

  public void stopServer(ActionEvent event) {
    LOGGER.info("stopping server");

    toggleButtons();
  }

  private void toggleButtons() {
    boolean isRunning = this.startServerButton.isDisabled();

    this.startServerButton.setDisable(!isRunning);
    this.stopServerButton.setDisable(isRunning);
  }
}
