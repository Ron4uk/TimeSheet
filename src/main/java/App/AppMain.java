package App;

import DB.HandlerDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application {
  public static HandlerDB handlerDB =new HandlerDB();

    public static void main(String[] args) {
        handlerDB.openConnect();
        Application.launch(args);

    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Подключение к Базе Данных");
        Parent root = FXMLLoader.load(AppMain.class.getResource("/startPage.fxml"));
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        handlerDB.closeConnect();
     }
}
