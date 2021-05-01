import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class StreamingServer extends Application {

  private static final Logger LOGGER = LogManager.getLogger(StreamingServer.class);
  private static final String LOG_TEXT_AREA = "#textAreaLogger";

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("StreamingServer.fxml"));
      Scene scene = new Scene(root);
      TextAreaAppender.textArea = (TextArea) scene.lookup(LOG_TEXT_AREA);
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
