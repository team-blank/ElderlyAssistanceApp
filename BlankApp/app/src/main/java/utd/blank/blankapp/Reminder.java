package utd.blank.blankapp;

/**
 * Created by atvaccaro on 11/3/15.
 */
public class Reminder {
    public int id;
    public String name;
    public String description;
    public int category;
    public int hour;
    public int minute;

    public Reminder() {
        this(0, "test_name", "test_description", 0, 12, 0);
    }

    public Reminder(int id, String name, String description, int category, int hour, int minute) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.hour = hour;
        this.minute = minute;
    }
}
