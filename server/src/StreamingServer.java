import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StreamingServer extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    this.initStage(stage);
    stage.show();
  }

  private void initStage(Stage stage) {
    Group root = new Group();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    
    stage.setTitle("Streaming Server");
    // stage.setResizable(false);
    stage.setHeight(500);
    stage.setWidth(500);
  }
}
