package Controllers;

import DB.Departments;
import DB.Employee;
import DB.TimeSheet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

import static App.AppMain.handlerDB;

public class TimeSheetController {
    protected List<Departments> listDepartments=new ArrayList<>();
    protected List<Employee> listEmployees=new ArrayList<>();
    protected List<TimeSheet> listTimeSheet=new ArrayList<>();
    protected List<TimeSheet> timeSheetsForSave=new ArrayList<>();
    protected Map<String, String> changeInStatusEmpl;
    protected List<String> listStatus=new ArrayList<>();
    protected List<String> listHolidays=new ArrayList<>();
    protected static String year="2020";

    @FXML
    VBox departments;
    @FXML
    TabPane tabepane;
    @FXML
    AnchorPane substrate=new AnchorPane();
    @FXML
    ScrollPane legendScroll;
    @FXML
    ScrollPane generalView;
    @FXML
    Button saveButton;
    @FXML
    Button logOut;
    @FXML
    Label resultLabel;
    @FXML
    VBox noteBox;

    public TimeSheetController() {

    }


    @FXML
    public void initialize(){
        listHolidays=handlerDB.getHolidays();
        saveButton.setDisable(true);
        tabepane.setDisable(true);
        noteBox.setAlignment(Pos.BOTTOM_CENTER);
        final ToggleGroup group = new ToggleGroup();
        listDepartments = handlerDB.getDepartments();
        listStatus=handlerDB.getAllStatus();
        for(Departments dep:listDepartments){
            ToggleButton toggleButton = new ToggleButton(dep.getName());  // добавляем кнопки с названием департаментов
            toggleButton.setOnAction(event -> {
                if(toggleButton.isSelected()) {
                    tabepane.setDisable(false);
                    resultLabel.setText("");
                    saveButton.setDisable(true);
                    listEmployees=handlerDB.getEmployees(dep.getName());    //получаем список работников из БД
                    fillEmployees(listEmployees, tabepane.getSelectionModel().getSelectedItem().getId());                           //заполняем Grid Pane ФИО, должность и Таб. №  и отметки по дням из БД

            }
                else toggleButton.setSelected(true);
            });
            toggleButton.setMaxWidth(200);
            toggleButton.setWrapText(true);
            toggleButton.setToggleGroup(group);
            departments.getChildren().add(toggleButton);
        }
        fillLegend();
        logOut.setOnAction(event -> logOut(event));

    }

    public void logOut(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выход из учетной записи");
        alert.setHeaderText("Вы уверены что хотите выйти? Вся несохраненная информация в БД будет удалена");
        alert.setResizable(false);
        alert.setContentText("Нажмите ОК для подтверждения или Cancel для возврата");
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/startPage.fxml"));
                Parent editRoot =(Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Подключение к Базе Данных");
                stage.setScene(new Scene(editRoot, 600, 500));
                stage.setResizable(false);
                stage.show();
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void printOnClick(){                 //обновление данных при переключении вкладок месяца
        if(tabepane.getSelectionModel().getSelectedItem().isSelected() && listEmployees.size()>0) {
                if(!saveButton.isDisable()) saveButton.setDisable(true);
                fillEmployees(listEmployees, tabepane.getSelectionModel().getSelectedItem().getId());
        };
    }

    public void fillEmployees(List<Employee> employees, String month){
        changeInStatusEmpl = new HashMap<>();
        saveButton.setDisable(true);
        GridPane generalPane = new GridPane();
        generalPane.gridLinesVisibleProperty().set(true);
        generalPane.prefWidthProperty().bind(generalView.widthProperty());
        generalPane.setStyle("-fx-background-color: #4f4f4f; -fx-padding: 2; -fx-hgap: 2; -fx-vgap: 2;");
        generalView.setContent(generalPane);

        Label flpLabel = new Label("ФИО");
        flpLabel.setWrapText(true);
        flpLabel.setMinWidth(200);
        flpLabel.setAlignment(Pos.CENTER);
        flpLabel.setMaxWidth(Double.MAX_VALUE);
        flpLabel.getStyleClass().add("titleStyle");

        Label positionLabel = new Label("Должность");
        positionLabel.setWrapText(true);
        positionLabel.setMinWidth(100);
        positionLabel.setMaxWidth(Double.MAX_VALUE);
        positionLabel.setAlignment(Pos.CENTER);
        positionLabel.getStyleClass().add("titleStyle");

        Label numberLabel = new Label("Табельный №");
        numberLabel.setWrapText(true);
        numberLabel.setMinWidth(100);
        numberLabel.setAlignment(Pos.CENTER);
        numberLabel.getStyleClass().add("titleStyle");
        numberLabel.setMaxWidth(Double.MAX_VALUE);

        generalPane.add(flpLabel, 0, 0);
        generalPane.add(positionLabel, 1, 0);
        generalPane.add(numberLabel, 2, 0);

        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(tabepane.getSelectionModel().getSelectedItem().getId()));
        int daysInMonth = yearMonthObject.lengthOfMonth();
        int gridSize = daysInMonth+3;

        for(int i=1; i<=daysInMonth; i++){

            Label numberoday = new Label(String.valueOf(i));
            numberoday.setMinWidth(35);
            numberoday.setAlignment(Pos.CENTER);

            if(isHolidays(year, month, String.valueOf(i))) numberoday.getStyleClass().add("holidayStyle");
            else numberoday.getStyleClass().add("oddStyle");
            generalPane.add(numberoday,i+2,0);

        }

        Label summaryLabel = new Label("Итого: ");
        summaryLabel.getStyleClass().add("titleStyle");
        summaryLabel.setAlignment(Pos.CENTER);
        summaryLabel.setMinWidth(150);
        generalPane.add(summaryLabel, gridSize,0);

        for(int i=0; i<employees.size(); i++){
            int numberday=0;
            listTimeSheet= handlerDB.getTimeSheet(year, month, String.valueOf(employees.get(i).getPersonal_number()));

            Label nameEmpl = new Label(employees.get(i).getLastname()+" "+employees.get(i).getFirstname()+" "+employees.get(i).getPatronymic());
            nameEmpl.setWrapText(true);
            nameEmpl.setPadding(new Insets(0,5,0,5));
            nameEmpl.setMaxWidth(Double.MAX_VALUE);
            nameEmpl.setMaxHeight(Double.MAX_VALUE);

            Label positionEmpl = new Label(employees.get(i).getPosition());
            positionEmpl.setWrapText(true);
            positionEmpl.setMinWidth(150);
            positionEmpl.setPadding(new Insets(0,5,0,5));
            positionEmpl.setMaxWidth(Double.MAX_VALUE);
            positionEmpl.setMaxHeight(Double.MAX_VALUE);

            Label numberEmpl = new Label(Integer.toString(employees.get(i).getPersonal_number()));
            numberEmpl.setWrapText(true);
            numberEmpl.setAlignment(Pos.CENTER);
            numberEmpl.setMaxWidth(Double.MAX_VALUE);
            numberEmpl.setMaxHeight(Double.MAX_VALUE);

            if(i%2==0){
                nameEmpl.getStyleClass().add("evenStyleNoDate");
                positionEmpl.getStyleClass().add("evenStyleNoDate");
                numberEmpl.getStyleClass().add("evenStyleNoDate");

            }
            else{
                nameEmpl.getStyleClass().add("oddStyleNoDate");
                positionEmpl.getStyleClass().add("oddStyleNoDate");
                numberEmpl.getStyleClass().add("oddStyleNoDate");
            }

            generalPane.add(nameEmpl, 0, i+1);
            generalPane.add(positionEmpl, 1, i+1);
            generalPane.add(numberEmpl, 2, i+1);
            GridPane.setHalignment(numberEmpl, HPos.CENTER);
            GridPane.setHalignment(positionEmpl, HPos.CENTER);

            for(int k=3; k<gridSize; k++) {
                TextField dayStatus=new TextField();
                dayStatus.setMinWidth(35);
                dayStatus.setMaxHeight(50);
                String index= String.valueOf(i+1)+k;
                dayStatus.setId(index);
                if(isHolidays(year, month, String.valueOf(k-2))) dayStatus.getStyleClass().add("holidayStyle");
                else{
                    if(i%2==0){
                        dayStatus.getStyleClass().add("evenStyle");
                    }
                    else dayStatus.getStyleClass().add("oddStyle");
                }

                ContextMenu contextMenu = new ContextMenu();

                for(String s:listStatus){
                    MenuItem menuItem = new MenuItem(s);
                    menuItem.setOnAction(event -> {
                        if(saveButton.isDisable()) saveButton.setDisable(false);
                        dayStatus.setText(s);
                        dayStatus.requestFocus();

                    });
                    contextMenu.getItems().add(menuItem);
                }
                MenuItem menuItemDel = new MenuItem("Удалить");
                menuItemDel.setOnAction(event -> {

                    dayStatus.clear();
                });
                contextMenu.getItems().add(menuItemDel);
                dayStatus.setContextMenu(contextMenu);
                dayStatus.setId(String.valueOf(employees.get(i).getPersonal_number())+" "+year+"-"+month+"-"+ (k-2));

                if(numberday<listTimeSheet.size() && Integer.parseInt(listTimeSheet.get(numberday).getDay())+2==k){
                    dayStatus.setText(listTimeSheet.get(numberday).getStatus());
                    generalPane.add(dayStatus, k, i+1);
                    numberday++;
                }
                else {
                    generalPane.add(dayStatus, k, i+1);
                }
                dayStatus.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
                    String newText = change.getControlNewText();

                    if (!listStatus.contains(newText) && newText.length()>0) {
                        return null ;
                    }
                    else {
                        return change ;
                    }
                }));

                dayStatus.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        saveButton.setDisable(false);
                        String  result = newValue.toString();
                        changeInStatusEmpl.put(dayStatus.getId(), dayStatus.getText());
                    }});

            }
            fillSummary(employees.get(i), year, month, generalPane, i+1, gridSize);

            }
        saveButton.setOnAction(event -> {
            saveStatusInDB(employees, month);
    });
    }


    public void fillSummary(Employee employee, String year, String month, GridPane pane, int rowNumber, int column){

        Map<String, Integer> summaryDayOfStatus = handlerDB.getSummary(employee, year, month);
        int count=0;
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> itr= summaryDayOfStatus.entrySet().iterator();
        while (itr.hasNext()){
            Map.Entry<String, Integer> pair = itr.next();
            String status = pair.getKey();
            Integer amount = pair.getValue();
            if(status != null) sb.append(status+"("+amount+")"+": ");
        }
        Label label = new Label(sb.toString());
        label.setWrapText(true);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxWidth(Double.MAX_VALUE);
        if(rowNumber%2==0){
            label.getStyleClass().add("oddStyleNoDate");
        }
        else label.getStyleClass().add("evenStyleNoDate");
        pane.add(label, column,rowNumber);

    }

    public void fillLegend(){
        Map<String, String> legend = handlerDB.getLegend();
        Iterator<Map.Entry<String, String>> itr = legend.entrySet().iterator();
        VBox legendBox = new VBox();
        legendScroll.setPrefSize(400, 100);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setStyle("-fx-background-color: #b3b3b3;");
        legendBox.getChildren().add(new Label("Легенда"));

        legend.forEach((k,v)->{
            HBox hBoxLeg = new HBox();
            Label keyLabel = new Label(k);
            keyLabel.setWrapText(true);
            keyLabel.setMinWidth(20);
            keyLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            keyLabel.setPadding(new Insets(2));
            Label valueLabel = new Label(" - "+v);
            valueLabel.setWrapText(true);
            valueLabel.setPadding(new Insets(2));
            hBoxLeg.getChildren().addAll(keyLabel, valueLabel);
            legendBox.getChildren().add(hBoxLeg);
        });

        legendScroll.setContent(legendBox);
        legendScroll.setFitToWidth(true);
        legendScroll.setFitToHeight(false);
        legendScroll.setStyle("-fx-border-width: 2;" + "-fx-border-insets: 2;"
                + "-fx-border-color: gray;");

    }

    public  boolean isHolidays (String year, String month, String day){
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date sqlDate = new Date(df.parse(year+"-"+month+"-" +day).getTime());
            if(listHolidays.contains(sqlDate.toString())) return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveStatusInDB(List<Employee> employees, String month){
        changeInStatusEmpl.forEach((k,v)-> {
            try {
                String[] num_date = k.split(" ");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date sqlDate = new Date(df.parse(num_date[1]).getTime());
                timeSheetsForSave.add(new TimeSheet(num_date[0], v, sqlDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        if(handlerDB.saveTimeShets(timeSheetsForSave)) resultLabel.setText("Изменения в БД прошли успешно");
        else resultLabel.setText("Ошибка записи в БД");
        fillEmployees(employees, month);
        saveButton.setDisable(true);
    }

}
