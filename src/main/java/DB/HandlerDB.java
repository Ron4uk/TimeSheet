package DB;

import Controllers.BaseController;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class HandlerDB {
    private String url;
    protected ResultSet resultSet;
    protected Connection connection;
    protected Statement statement;

    public HandlerDB() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void openConnect(){
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("resources\\database.property"))){
            props.load(in);
            url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");
            Class.forName(props.getProperty("driver")).newInstance();
            connection = DriverManager.getConnection(this.getUrl(), username, password);
            statement = connection.createStatement();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeConnect(){
        try {
            statement.close();
            connection.close();
            System.out.println ("Приложение отключилось от БД !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Departments> getDepartments(){
        List<Departments> listDep = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT dep_name FROM departments");
            while (resultSet.next()){
                listDep.add(new Departments( resultSet.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Logging
        }
        return listDep;

    }

    public List<Employee> getEmployees(String department){
        List<Employee> listEmp = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT first_name, last_name, patronymic, position_name, personal_number, birthday, remote_work FROM employees as e " +
                    "JOIN departments AS d ON e.dep_id=d.id " +
                    "JOIN positions AS p ON p.id = e.id_pos " +
                    "WHERE d.dep_name=\""+department+"\"");
            while (resultSet.next()){
            listEmp.add(new Employee(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                    resultSet.getInt(5), resultSet.getDate(6),resultSet.getBoolean(7), department ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listEmp;
    }



    public List<TimeSheet> getTimeSheet(String year, String month, String personal_number){
        List<TimeSheet> listTimeSheet=new ArrayList<>();
     try {
         resultSet = statement.executeQuery("SELECT personal_number, DAY(mark_day), status_day FROM timesheet AS t " +
                 "JOIN employees AS e ON e.id=t.emp_id " +
                 "WHERE personal_number=\""+personal_number+"\" AND MONTH(mark_day)=\""+month+"\" AND YEAR(mark_day)=\""+year+"\" ORDER BY DAY(mark_day)");
         while (resultSet.next()){
             listTimeSheet.add(new TimeSheet(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
         }
     }
     catch (SQLException e){
         e.printStackTrace();
     }
        return listTimeSheet;
    }

    public Map<String, Integer> getSummary(Employee employee, String year, String month){
        Map<String, Integer> summaryDayOfStatus = new HashMap<>();
        try{
            resultSet = statement.executeQuery("SELECT status_day, COUNT(DAY(mark_day)) AS amount FROM timesheet AS t " +
                    "JOIN employees AS e ON e.id=t.emp_id " +
                    "WHERE  MONTH(mark_day)=\""+month+"\" AND YEAR(mark_day)= \""+year+"\" AND personal_number= \""+employee.getPersonal_number()+ "\" GROUP BY status_day");

            while (resultSet.next()){
                if(summaryDayOfStatus.containsKey(resultSet.getString(1))){
                    summaryDayOfStatus.put(resultSet.getString(1), summaryDayOfStatus.get(resultSet.getString(1))+1);
                }
                else summaryDayOfStatus.put(resultSet.getString(1), Integer.parseInt(resultSet.getString(2)));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return summaryDayOfStatus;
    }

    public Map<String, String> getLegend(){
        Map<String, String> legend= new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT daytype, type_describe FROM status");
            while (resultSet.next()){
                legend.put(resultSet.getString(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return legend;
    }

    public List<String> getAllPositions(){
        List<String> allPositions = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT position_name FROM positions");
            while (resultSet.next()){
                allPositions.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allPositions;
    }

    public List<String> getAllStatus(){
        List<String> allStatus = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT daytype FROM status");
            while (resultSet.next()){
                allStatus.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allStatus;
    }


    public boolean changeDepartmentName(String oldName, String newName){
        try {
            int a = statement.executeUpdate("UPDATE departments SET dep_name = \""+newName+"\" WHERE dep_name =\""+oldName+"\"");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addDepartment(String department){
        try {
            int a = statement.executeUpdate("INSERT INTO departments SET dep_name = \""+department+"\"");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean deleteDepartment(String department){
        try {
            int a = statement.executeUpdate("DELETE FROM departments WHERE dep_name = \""+department+"\"");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addOrEditEmploye(Employee employee){
        try {
            int a = statement.executeUpdate("INSERT INTO employees (dep_id, first_name,last_name,patronymic, personal_number, id_pos, birthday,remote_work) " +
                    "VALUES ((SELECT id FROM departments WHERE dep_name=\""+employee.getDepartment()+"\"),\""+employee.getFirstname()+"\",\""+employee.getLastname()+"\", \""+employee.getPatronymic()+"\","+employee.getPersonal_number()+
                    ",(SELECT id FROM positions WHERE position_name=\""+employee.getPosition()+"\"),\""+employee.getDate()+"\","+employee.isRemoteWork()+") "+
                    "ON DUPLICATE KEY UPDATE dep_id = (SELECT id FROM departments WHERE dep_name=\""+employee.getDepartment()+"\"), " +
                    "first_name=\""+employee.getFirstname()+"\", " +
                    "last_name=\""+employee.getLastname()+"\", " +
                    "patronymic=\""+employee.getPatronymic()+"\", " +
                    "personal_number= "+employee.getPersonal_number()+", " +
                    "id_pos=(SELECT id FROM positions WHERE position_name=\""+employee.getPosition()+"\"), " +
                    "birthday=\""+employee.getDate()+"\", " +
                    "remote_work="+employee.isRemoteWork());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteEmployee (int personal_number){
        try {
            int a = statement.executeUpdate("DELETE FROM employees WHERE personal_number = "+personal_number);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Employee> findEmployees(String lastName){
        List<Employee> listEmp = new ArrayList<>();
        try {
              resultSet = statement.executeQuery("SELECT first_name, last_name, patronymic, position_name, personal_number, birthday, remote_work, IF(dep_id IS NULL, NULL, (SELECT dep_name FROM departments WHERE dep_id=id ))" +
                    " FROM employees as e " +
                    "" +
                    "JOIN positions AS p ON p.id = e.id_pos " +
                    "WHERE last_name=\""+lastName+"\"");

            while (resultSet.next()){
                listEmp.add(new Employee(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                        resultSet.getInt(5), resultSet.getDate(6),resultSet.getBoolean(7), resultSet.getString(8) ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listEmp;
    }

    public boolean saveTimeShets (List<TimeSheet> timeSheets){
        try {
            for (TimeSheet ts: timeSheets) {
                int a = statement.executeUpdate("INSERT INTO timesheet (emp_id, emp_number, mark_day, status_day) " +
                        "VALUES ((SELECT id FROM employees WHERE personal_number=\"" + ts.getNumber() + "\"),\"" + ts.getNumber() + "\",\"" + ts.getDate() + "\", "+(ts.getStatus().length()>0? "\"" + ts.getStatus() + "\") ":"NULL) ") +
                        "ON DUPLICATE KEY UPDATE "+
                        "status_day = "+(ts.getStatus().length()>0? "\"" + ts.getStatus() + "\"":"NULL"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String>  getHolidays(){
        List<String> holidays = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT holiday FROM holidays");
            while (resultSet.next()){
                holidays.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holidays;
    }
}
