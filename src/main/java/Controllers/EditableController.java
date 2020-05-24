package Controllers;

import DB.Departments;
import DB.Employee;
import DB.TimeSheet;
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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import static App.AppMain.handlerDB;

public class EditableController {
    protected List<Departments> listDepartments=new ArrayList<>();
    protected List<Employee> listEmployees=new ArrayList<>();
    protected List<TimeSheet> listTimeSheet=new ArrayList<>();
    protected Map<String, String> changeInStatusEmpl;
    protected List<String> listStatus=new ArrayList<>();
    protected List<String> listHolidays=new ArrayList<>();
    protected static String year="2020"; //TODO CHANGE IT

    @FXML
    VBox departments;
    @FXML
    TabPane tabepane;
    @FXML
    ScrollPane legendScroll;
    @FXML
    ScrollPane generalView;

    @FXML
    VBox noteBox;

    public EditableController() {

    }

    @FXML
    public void initialize(){
        listHolidays=handlerDB.getHolidays();
        tabepane.setDisable(true);
        noteBox.setAlignment(Pos.BOTTOM_CENTER);
        final ToggleGroup group = new ToggleGroup();
        listDepartments = handlerDB.getDepartments();
        listStatus=handlerDB.getAllStatus();

        Button editDep = new Button("Редактировать \"департаметы\"");
        Button editEmpl = new Button("Редактировать \"сотрудники\'");
        Button logOut = new Button("Выход");
        logOut.setMaxWidth(Double.MAX_VALUE);
        editDep.setMaxWidth(Double.MAX_VALUE);
        editEmpl.setMaxWidth(Double.MAX_VALUE);
        editDep.setOnAction(event -> {
            editDepartment(event);
        });
        editEmpl.setOnAction(event -> {
            editEmployees();
        });
        noteBox.getChildren().addAll(editDep, editEmpl, logOut);

        for(Departments dep:listDepartments){
            ToggleButton toggleButton = new ToggleButton(dep.getName());  // добавляем кнопки с названием департаментов
            toggleButton.setOnAction(event -> {
                if(toggleButton.isSelected()) {
                    tabepane.setDisable(false);
                    listEmployees=handlerDB.getEmployees(dep.getName());    //получаем список работников из БД
                    fillEmployees(listEmployees, tabepane.getSelectionModel().getSelectedItem().getId());    //заполняем Grid Pane ФИО, должность и Таб. №  и отметки по дням из БД

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

    @FXML
    public void printOnClick(){                 //обновление данных при переключении вкладок месяца
        if(tabepane.getSelectionModel().getSelectedItem().isSelected() && listEmployees.size()>0) {
            fillEmployees(listEmployees, tabepane.getSelectionModel().getSelectedItem().getId());
        };
    }

    public void fillEmployees(List<Employee> employees, String month){
        changeInStatusEmpl = new HashMap<>();
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

                dayStatus.setEditable(false);

                if(numberday<listTimeSheet.size() && Integer.parseInt(listTimeSheet.get(numberday).getDay())+2==k){
                    dayStatus.setText(listTimeSheet.get(numberday).getStatus());
                    generalPane.add(dayStatus, k, i+1);
                    numberday++;
                }
                else {
                    generalPane.add(dayStatus, k, i+1);
                }
            }
            fillSummary(employees.get(i), year, month, generalPane, i+1, gridSize);
        }

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
            java.sql.Date sqlDate = new java.sql.Date(df.parse(year+"-"+month+"-" +day).getTime());

            if(listHolidays.contains(sqlDate.toString())) return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void editDepartment(Event event){
        try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editableDepWindow.fxml"));
                Parent editRoot =(Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setMaxWidth(625);
                stage.setMaxHeight(500);
                stage.setMinWidth(625);
                stage.setMinHeight(500);
                stage.setTitle("Редактировать \"департаметы\"");
                stage.setScene(new Scene(editRoot, 625, 500));
                stage.show();
               ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void  editEmployees(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editableEmplWindow.fxml"));
            Parent editRoot =(Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Редактировать \"сотрудники\"");
            stage.setScene(new Scene(editRoot, 800, 500));
            stage.show();
            stage.setResizable(false);
           // ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

