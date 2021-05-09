import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import utils.TextAreaAppender;

public class StreamingServer extends Application {
  private static final String LOG_TEXT_AREA = "#textAreaLogger";
  private static final String START_SERVER_BUTTON = "#startServerButton";

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    try {
      Scene scene = initScene();
      
      TextAreaAppender.textArea = (TextArea) scene.lookup(LOG_TEXT_AREA);
      
      Button startServer = (Button) scene.lookup(START_SERVER_BUTTON);
      startServer.fire();
      
      stage.setScene(scene);
      stage.setTitle("Streaming Server");
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private Scene initScene() throws IOException {
    StreamingServerService service = new StreamingServerService();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("StreamingServer.fxml"));
    StreamingServerController controller = new StreamingServerController(service);
    loader.setController(controller);

    return new Scene(loader.load());
  }
}
