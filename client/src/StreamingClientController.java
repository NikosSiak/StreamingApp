import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

public class StreamingClientController implements Initializable {

  private final StreamingClientService service;

  @FXML
  private ChoiceBox<String> formatChoice;
  @FXML
  private ChoiceBox<String> videoChoice;
  @FXML
  private ChoiceBox<String> protocolChoice;

  @FXML
  private Button getVideosButton;
  @FXML
  private Button watchStreamButton;

  @FXML
  private Label videoLabel;
  @FXML
  private Label protocolLabel;

  @FXML
  private AnchorPane mainPane;
  @FXML
  private AnchorPane loadingPane;

  @FXML
  private ProgressIndicator speedTestProgressIndicator;

  private static final String[] formats = { "MP4", "AVI", "MKV" };
  private static final String[] protocols = { "TCP", "UDP", "RTP" };

  public StreamingClientController(StreamingClientService service) {
    this.service = service;
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    this.formatChoice.getItems().setAll(StreamingClientController.formats);
    this.protocolChoice.getItems().setAll(StreamingClientController.protocols);

    this.formatChoice.setValue(formats[0]);
    this.protocolChoice.setValue(protocols[0]);

    startSpeedTest();
  }

  public void getVideos(ActionEvent event) {
    String format = this.formatChoice.getSelectionModel().getSelectedItem();

    Consumer<String[]> videosLoaded = videos -> {
      Platform.runLater(() -> {
        this.videoChoice.getItems().setAll(videos);

        this.videoLabel.setDisable(false);
        this.videoChoice.setDisable(false);

        this.protocolLabel.setDisable(false);
        this.protocolChoice.setDisable(false);

        this.watchStreamButton.setDisable(false);
      });
    };

    this.service.getVideos(format, videosLoaded);
  }

  public void watchStream(ActionEvent event) {
    String video = this.videoChoice.getSelectionModel().getSelectedItem();
    String protocol = this.protocolChoice.getSelectionModel().getSelectedItem();

    this.service.watchStream(video, protocol);
  }

  private void startSpeedTest() {
    DoubleConsumer onProgressCallback = percent -> {
      Platform.runLater(() -> {
        this.speedTestProgressIndicator.setProgress(percent / 100f);
      });
    };

    Runnable onCompletionCallback = () -> {
      Platform.runLater(() -> {
        this.loadingPane.setVisible(false);
        this.mainPane.setVisible(true);
      });
    };

    this.service.startSpeedTest(onProgressCallback, onCompletionCallback);
  }
}
