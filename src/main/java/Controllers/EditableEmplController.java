package Controllers;

import DB.Departments;
import DB.Employee;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.sql.Date;
import java.time.YearMonth;
import java.util.*;
import static App.AppMain.handlerDB;

public class EditableEmplController {
    private String selectDepartment;
    private int selectIdEmpl;
    private  String selectedNameEmpl;
    protected List<Departments> listDepartments = new ArrayList<>();
    protected List<Employee> listEmployees=new ArrayList<>();

    @FXML
    ScrollPane scrollDep;
    @FXML
    ScrollPane scrollEmpl;
    @FXML
    VBox vboxDepartments;
    @FXML
    VBox vboxEmployees;
    @FXML
    Label noteLabel;
    @FXML
    Button add;
    @FXML
    Button delete;
    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    TextField patronymic;
    @FXML
    TextField personalNumber;
    @FXML
    TextField year;
    @FXML
    TextField month;
    @FXML
    TextField day;
    @FXML
    ChoiceBox depBox;
    @FXML
    ChoiceBox positionBox;
    @FXML
    CheckBox remoteCheck;
    @FXML
    Button clearButton;
    @FXML
    Label resultLabel;
    @FXML
    TextField searchField;
    @FXML
    Button searchButton;




    public EditableEmplController() {

    }

    public void initialize() {
        vboxDepartments.getChildren().clear();
        vboxEmployees.getChildren().clear();
        delete.setDisable(true);
        clearAllFileds();

        clearButton.setOnAction(event -> {
            clearAllFileds();
        });

        // START проверка и ограничения на ввод чисел в поля Год, Месяц, День
        year.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    year.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(newValue.length()>0){
                    Integer result = Integer.parseInt(newValue.toString());

                    if(result>Calendar.getInstance().get(Calendar.YEAR)-18 ) {
                    Platform.runLater(() -> {
                        year.clear();
                    });
                    }
                     if(newValue.length()>3 ) {
                         if (result < 1900) {
                             Platform.runLater(() -> {
                                 year.clear();
                             });
                         }
                     }
                    }
                if(!newValue.equals(oldValue)) day.clear();
                }
        });

        personalNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    personalNumber.setText(newValue.replaceAll("[^\\d]", ""));
                }


                }


        });
        year.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            if (newText.length() > 4) {
                return null ;
            } else {
                return change ;
            }
        }));
        month.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    month.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(newValue.length()>0){
                    Integer result = Integer.parseInt(newValue.toString());
                    if(result>12 || result==0) {
                        Platform.runLater(() -> {
                            month.clear();
                        });
                    }

                }
                if(!newValue.equals(oldValue)) day.clear();

            }
        });
        month.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            String monthText = change.getControlNewText();

            if (monthText.length() > 2) {
                return null ;
            } else {
                return change ;
            }
        }));

        day.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    day.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if(newValue.length()>0){
                    int daysInMonth;
                    if(year.getText().length()>0 && month.getText().length()>0) {
                        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year.getText()), Integer.parseInt(month.getText()));
                         daysInMonth = yearMonthObject.lengthOfMonth();
                    }
                    else{
                        YearMonth yearMonthObject = YearMonth.of(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)+1);
                        daysInMonth = yearMonthObject.lengthOfMonth();
                    }
                    Integer result = Integer.parseInt(newValue.toString());
                    if(result<1 || result>daysInMonth) {
                        Platform.runLater(() -> {
                            day.clear();
                        });
                    }

                }

            }
        });
        day.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            String monthText = change.getControlNewText();

            if (monthText.length() > 2) {
                return null ;
            } else {
                return change ;
            }
        }));
        // END проверка и ограничения на ввод чисел в поля Год, Месяц, День

        scrollEmpl.vvalueProperty().bindBidirectional(scrollDep.vvalueProperty());
        scrollDep.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        vboxDepartments.getChildren().clear();
        final ToggleGroup groupDep = new ToggleGroup();

        listDepartments = handlerDB.getDepartments();
        String[] arrayDep = new String[listDepartments.size()];
        for(int i=0; i<arrayDep.length;i++){
            arrayDep[i]=listDepartments.get(i).getName();
        }
        ObservableList<String> deps = FXCollections.observableArrayList(arrayDep);
        depBox.setItems(deps);

        String[] arrayPoss = handlerDB.getAllPositions().toArray( new String[handlerDB.getAllPositions().size()]);
        ObservableList<String> poss = FXCollections.observableArrayList(arrayPoss);
        positionBox.setItems(poss);
        for (Departments dep : listDepartments) {

            ToggleButton toggleButton = new ToggleButton(dep.getName());  // добавляем кнопки с названием департаментов
            toggleButton.setOnAction(event -> {
                if (toggleButton.isSelected()) {
                    selectDepartment=dep.getName();
                    noteLabel.setText("Выберите сотрудника");
                    listEmployees=handlerDB.getEmployees(selectDepartment);
                    loadEmployees();
                    resultLabel.setText("");
//
                } else toggleButton.setSelected(true);

            });
            toggleButton.setMaxWidth(200);
            toggleButton.setWrapText(true);
            toggleButton.setToggleGroup(groupDep);
            vboxDepartments.getChildren().add(toggleButton);
        }
        add.setOnAction(event->{
            addOrEditEmpl();
        });
        delete.setOnAction(event -> {
            deleteEmployee();
        });
        searchButton.setOnAction(event -> {
            findEmployee();
        });
    }

    public void loadEmployees(){
        final ToggleGroup groupEmpl = new ToggleGroup();
        vboxEmployees.getChildren().clear();

        for(Employee employee:listEmployees) {
            ToggleButton toggleButtonEmpl = new ToggleButton("№"+employee.getPersonal_number()+" "+employee.getLastname()+" "+employee.getFirstname()+" "+ employee.getPatronymic());
            toggleButtonEmpl.setOnAction(event -> {
                resultLabel.setText("");
                selectIdEmpl=employee.getPersonal_number();
                selectedNameEmpl=employee.getLastname()+" "+employee.getFirstname()+" "+employee.getPatronymic();
                delete.setDisable(false);
                firstName.setText(employee.getFirstname());
                lastName.setText(employee.getLastname());
                patronymic.setText(employee.getPatronymic());
                personalNumber.setText(String.valueOf(employee.getPersonal_number()));
                personalNumber.setEditable(false);
                String[] dmy = employee.getDate().toString().split("-");
                        year.setText(dmy[0]);
                        month.setText(dmy[1]);
                        day.setText(dmy[2]);
                depBox.setValue(employee.getDepartment());
                positionBox.setValue(employee.getPosition());
                if (employee.isRemoteWork()) {
                    remoteCheck.setSelected(true);
                } else {
                    remoteCheck.setSelected(false);
                }
            delete.setDisable(false);
            });
            toggleButtonEmpl.setMaxWidth(200);
            toggleButtonEmpl.setWrapText(true);
            toggleButtonEmpl.setToggleGroup(groupEmpl);
            vboxEmployees.getChildren().add(toggleButtonEmpl);
        }
    }

    public void addOrEditEmpl(){
        if(firstName.getText().length()==0 || lastName.getText().length()==0 || personalNumber.getText().length()==0 || year.getText().length()==0 ||  month.getText().length()==0 || day.getText().length()==0 ||
        depBox.getValue()==null || positionBox.getValue()==null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не заполнены все необходимые поля. Проверьте поля отмеченные * и попробуйте снова.");
            alert.setResizable(false);
            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
        }
        else {
            Date date= Date.valueOf(year.getText()+"-"+month.getText()+"-"+day.getText());
            Employee employee = new Employee(firstName.getText(), lastName.getText(), patronymic.getText().length()==0? null:patronymic.getText(), positionBox.getValue() == null? null:positionBox.getValue().toString(), Integer.parseInt(personalNumber.getText()), date, remoteCheck.isSelected(), depBox.getValue()== null? null:depBox.getValue().toString() );
            if (handlerDB.addOrEditEmploye(employee)) {
                initialize();
                resultLabel.setText("Изменения в БД прошли успешно");
            }
            else resultLabel.setText("Ошибка записи в БД ");
        }
    }

    public void clearAllFileds(){
        personalNumber.setEditable(true);
        year.clear();
        month.clear();
        day.clear();
        firstName.clear();
        lastName.clear();
        patronymic.clear();
        remoteCheck.setSelected(false);
        positionBox.getSelectionModel().clearSelection();
        depBox.getSelectionModel().clearSelection();
        personalNumber.clear();
    }


    public void deleteEmployee(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление сотрудника из БД");
        alert.setHeaderText("Вы уверены что хотите удалить сотрудника табельный номер №"+selectIdEmpl+" ФИО: "+selectedNameEmpl);
        alert.setResizable(false);
        alert.setContentText("Нажмите ОК для подтверждения или Cancel для возврата");
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            if(handlerDB.deleteEmployee(selectIdEmpl)) {
                initialize();
                resultLabel.setText("Изменения в БД прошли успешно");
            }
            else{
                resultLabel.setText("Ошибка удаления в БД");
            }
        }
    }

    public void findEmployee(){
            listEmployees=handlerDB.findEmployees(searchField.getText());
            loadEmployees();
    }
}
