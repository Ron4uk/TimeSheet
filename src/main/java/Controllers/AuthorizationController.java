package Controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;


public class AuthorizationController {
    @FXML
    Button viewer;
    @FXML
    Button tableViewer;
    @FXML
    Button editor;
    @FXML
    VBox vBox;

    public void initialize() {
        vBox.setStyle("-fx-background-color: #b5b8b1;");
        viewer.setStyle(" -fx-background-radius:1em;");
        tableViewer.setStyle(" -fx-background-radius:1em;");
        editor.setStyle(" -fx-background-radius:1em;");

        viewer.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/base.fxml"));
                Parent editRoot =(Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setMinWidth(900);
                stage.setMinHeight(650);
                stage.setTitle("Просмотр табеля");
                Scene scene = new Scene(editRoot, 900, 650);
                scene.getStylesheets().add(getClass().getResource("/mycss.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editor.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editableBase.fxml"));
                Parent editRoot =(Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setMinWidth(900);
                stage.setMinHeight(650);
                stage.setTitle("Редактирование департаментов/сотрудников");
                Scene scene = new Scene(editRoot, 900, 650);
                scene.getStylesheets().add(getClass().getResource("/mycss.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tableViewer.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editableTimeSheet.fxml"));
                Parent editRoot =(Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setMinWidth(900);
                stage.setMinHeight(650);
                stage.setTitle("Редактирование табеля");
                Scene scene = new Scene(editRoot, 900, 650);
                scene.getStylesheets().add(getClass().getResource("/mycss.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
