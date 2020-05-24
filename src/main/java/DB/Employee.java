package DB;


import java.sql.Date;

public class Employee {
    private String firstname;
    private String lastname;
    private String patronymic;
    private String position;
    private int personal_number;
    private Date date;
    private boolean remoteWork;
    private String department;

    public Employee() {
    }

    public Employee(String firstname, String lastname, String patronymic, String position, int personal_number, Date date, boolean remoteWork, String department) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.position = position;
        this.personal_number = personal_number;
        this.date = date;
        this.remoteWork = remoteWork;
        this.department = department;
    }

    public Employee(String firstname, String lastname, String patronymic, int personal_number) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.personal_number = personal_number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRemoteWork() {
        return remoteWork;
    }

    public void setRemoteWork(boolean remoteWork) {
        this.remoteWork = remoteWork;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Employee(String firstname, String lastname, String patronymic, String position, int personal_number) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.position = position;
        this.personal_number = personal_number;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronymic() {
        if(patronymic==null) return "";
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public int getPersonal_number() {
        return personal_number;
    }

    public void setPersonal_number(int personal_number) {
        this.personal_number = personal_number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
       return new String(this.getFirstname()+" "+this.getLastname()+" "+this.getPatronymic()+" "+this.getPosition()+" "+this.getPersonal_number()+" "+this.getDate()+" "+this.isRemoteWork()+" "+this.getDepartment());

    }
}
