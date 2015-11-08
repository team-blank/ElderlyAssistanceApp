package utd.blank.blankapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String REMINDER_NAME_MESSAGE = "com.utd.reminder.REMINDER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        List<Reminder> reminders = ReminderDatabaseHelper.getInstance(this).getAllReminders();
        List<String> names = new ArrayList<>();

        for (Reminder reminder : reminders) {
            names.add(reminder.name);
        }

//        //testing some db stuff
//        Reminder newReminder = new Reminder();
//
//        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
//        dbHelper.addReminder(newReminder);
//
//        List<Reminder> reminders = dbHelper.getAllReminders();
//        for (Reminder reminder : reminders) {
//            System.out.println("Name:" + reminder.name + " Desc:" + reminder.description + " H:" +
//                reminder.hour + " M:" + reminder.minute);
//        }

       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListViewListener(this, listView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void createReminder(View view) {
        Intent intent = new Intent(this, CreateReminderActivity.class);
        intent.putExtra(REMINDER_NAME_MESSAGE, Integer.toString(-1));
        startActivity(intent);
    }

    private class ListViewListener implements OnItemClickListener {
        Activity parent;
        ListView listView;

        public ListViewListener(Activity parent, ListView listView) {
            super();
            this.parent = parent;
            this.listView = listView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int itemPosition = position;

            String reminderName = (String) listView.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(),
                    "Pos:" + itemPosition + " item: " + reminderName + " id:" + id, Toast.LENGTH_LONG)
                    .show();

            Intent intent = new Intent(this.parent, CreateReminderActivity.class);
//            intent.putExtra(REMINDER_NAME_MESSAGE, reminderName);
            intent.putExtra(REMINDER_NAME_MESSAGE, Integer.toString((int)id));    //yolo
            startActivity(intent);
        }
    }   //end class ListViewListener
}
