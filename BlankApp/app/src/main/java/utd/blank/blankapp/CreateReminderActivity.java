package utd.blank.blankapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.List;

public class CreateReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveReminder(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        Reminder r = new Reminder();
        EditText editText = (EditText) findViewById(R.id.editText);
        r.name = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editText2);
        r.description = editText.getText().toString();
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        r.hour = timePicker.getHour();
        r.minute = timePicker.getMinute();

        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
        dbHelper.addReminder(r);

        List<Reminder> reminders = dbHelper.getAllReminders();
        for (Reminder reminder : reminders) {
            System.out.println("Name:" + reminder.name + " Desc:" + reminder.description + " H:" +
                    reminder.hour + " M:" + reminder.minute);
        }

        startActivity(intent);
    }
}
