package utd.blank.blankapp;

/**
 * Created by atvaccaro on 11/3/15.
 */
public class Reminder {
    public String name;
    public String description;
    public int hour;
    public int minute;

    public Reminder() {
        this("test_name", "test_description", 12, 0);
    }

    public Reminder(String name, String description, int hour, int minute) {
        this.name = name;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
    }
}
