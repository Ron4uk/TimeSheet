package Controllers;

import DB.Departments;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static App.AppMain.handlerDB;

public class EditableDepController {

    protected List<Departments> listDepartments=new ArrayList<>();
    private String selectDepartment;
    private String editDepartment;
    private String addDepartment;

    @FXML
    VBox vboxDepartments;
    @FXML
    Button edit;
    @FXML
    Button add;
    @FXML
    Button delete;
    @FXML
    Label labelDep;
    @FXML
    Label labelDepCur;
    @FXML
    TextField editTextField;
    @FXML
    Button writeEditDep;
    @FXML
    TextField addTextField1;
    @FXML
    Label resultLabel;
    @FXML
    Button logOut;



    public EditableDepController() {

    }

    public void initialize(){
        resultLabel.setText("");
        vboxDepartments.getChildren().clear();
        final ToggleGroup group = new ToggleGroup();
        listDepartments = handlerDB.getDepartments();
        edit.setDisable(true);
        add.setDisable(true);
        editTextField.setDisable(true);
        delete.setDisable(true);
        writeEditDep.setDisable(true);
        logOut.setOnAction(event -> {
            logOut(event);
        });
        for(Departments dep:listDepartments){
            ToggleButton toggleButton = new ToggleButton(dep.getName());  // добавляем кнопки с названием департаментов
            toggleButton.setOnAction(event -> {
                if(toggleButton.isSelected()) {
                    resultLabel.setText("");
                    edit.setDisable(false);
                    delete.setDisable(false);
                    selectDepartment=dep.getName();
                }
                else edit.setDisable(true);
            });
            toggleButton.setMaxWidth(200);
            toggleButton.setWrapText(true);
            toggleButton.setToggleGroup(group);
            vboxDepartments.getChildren().add(toggleButton);
        }

        edit.setOnAction(event -> {
            resultLabel.setText("");
            labelDep.setDisable(false);
            labelDep.setWrapText(true);
            labelDepCur.setDisable(false);
            labelDepCur.setWrapText(true);
            labelDepCur.setText("\'"+selectDepartment+"\"  на:");
            editTextField.setEditable(true);
            editTextField.setDisable(false);
            editTextField.setOnKeyPressed(event1 -> {
                writeEditDep.setDisable(false);
            });

        });

        writeEditDep.setOnAction(event -> {
            resultLabel.setText("");
            editDepartment =editTextField.getText();
            boolean result= handlerDB.changeDepartmentName(selectDepartment, editDepartment);
            if (result) {
                initialize();
                resultLabel.setText("Изменения в БД прошли успешно");
            } else {
                resultLabel.setText("Ошибка записи в БД ");
            }

            editTextField.clear();
            labelDepCur.setText("");
            labelDep.setText("");
        });

        addTextField1.setOnKeyPressed(event -> {
            add.setDisable(false);
        });

        add.setOnAction(event -> {
            addDepartment=addTextField1.getText();
            if(handlerDB.addDepartment(addDepartment)) {
                addTextField1.clear();
                initialize();
                resultLabel.setText("Изменения в БД прошли успешно");
            }
            else resultLabel.setText("Ошибка записи в БД");
        });

        delete.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление департамента из БД");
            alert.setHeaderText("Вы уверены что хотите удалить департамент: "+selectDepartment);
            alert.setResizable(false);
            alert.setContentText("Нажмите ОК для подтверждения или Cancel для возврата");
            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK) {
                if(handlerDB.deleteDepartment(selectDepartment)) {
                    initialize();
                    resultLabel.setText("Изменения в БД прошли успешно");
                }
                else{
                    resultLabel.setText("Ошибка удаления в БД");
                }
            }
        });
    }

    public void logOut(Event event) {
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

    }
}
