import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StreamingClient extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    try {
      Scene scene = initScene();
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private Scene initScene() throws IOException {
    StreamingClientService service = new StreamingClientService();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("StreamingClient.fxml"));
    StreamingClientController controller = new StreamingClientController(service);
    loader.setController(controller);

    return new Scene(loader.load());
  }
  
}
