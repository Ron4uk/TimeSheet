package DB;

import java.sql.Date;

public class TimeSheet {
    protected String number;
    protected String day;
    protected String status;
    protected Date date;

    public TimeSheet() {
    }

    public TimeSheet(String number, String day, String status) {
        this.number = number;
        this.day = day;
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TimeSheet(String number, String status, Date date) {
        this.number = number;
        this.status = status;
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
