package utd.blank.blankapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    public final static String REMINDER_ID_MESSAGE = "com.utd.reminder.REMINDER_ID";
    private int[] reminderIDs = new int[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        List<Reminder> reminders = ReminderDatabaseHelper.getInstance(this).getAllReminders();
        List<String> names = new ArrayList<>();
        reminderIDs = new int[reminders.size()];
        int i = 0;
        Reminder soonest = null;
        if (reminders.size() > 0) {

            soonest = reminders.get(0);    //for getting next reminder to schedule
            Date currentDate = Calendar.getInstance().getTime();
            for (Reminder reminder : reminders) {
                String n = "" + String.format("%02d", reminder.hour) + ":" + String.format("%02d", reminder.minute) + " " + reminder.name;
                names.add(n);
                reminderIDs[i++] = reminder.id;

                if ((reminder.hour < currentDate.getHours() ? reminder.hour + 24 : reminder.hour) - currentDate.getHours()
                        <=
                        (soonest.hour < currentDate.getHours() ? soonest.hour + 24 : soonest.hour) - currentDate.getHours()) {
                    if ((reminder.minute < currentDate.getMinutes() ? reminder.minute + 60 : reminder.minute) - currentDate.getMinutes()
                            <=
                            (soonest.minute < currentDate.getMinutes() ? soonest.minute + 60 : soonest.minute) - currentDate.getMinutes()) {
                        soonest = reminder;
                    }
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListViewListener(this, listView));

        Spinner s = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, CreateReminderActivity.categories);
        s.setAdapter(spinnerAdapter);
        s.setOnItemSelectedListener(new CategorySpinnerListener(adapter, this));

        if (soonest != null) {
            Log.d(TAG, "Scheduled " + soonest.name + " for " + soonest.hour + ":" + soonest.minute);
            ReminderBroadcastReceiver.setAlarm(this, soonest);
        }
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
            Intent i = new Intent(this, ReminderPreferenceActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createReminder(View view) {
        Intent intent = new Intent(this, CreateReminderActivity.class);
        intent.putExtra(REMINDER_ID_MESSAGE, Integer.toString(-1));
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
            int reminderID = reminderIDs[(int) id];

            String reminderName = (String) listView.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(),
                    "Pos:" + position + " reminderID:" + reminderID, Toast.LENGTH_LONG)
                    .show();

            Intent intent = new Intent(this.parent, CreateReminderActivity.class);
//            intent.putExtra(REMINDER_ID_MESSAGE, reminderName);
            intent.putExtra(REMINDER_ID_MESSAGE, (int) reminderID);    //yolo
//            Log.d(TAG, String.format("id: %d reminderID: %d", (int) id, reminderID));
            startActivity(intent);
        }
    }   //end class ListViewListener

    private class CategorySpinnerListener implements AdapterView.OnItemSelectedListener {
        ArrayAdapter<String> adapter;
        Activity parent;

        public CategorySpinnerListener(ArrayAdapter<String> a, Activity parent) {
            this.adapter = a;
            this.parent = parent;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            List<Reminder> reminders = ReminderDatabaseHelper.getInstance(this.parent).getRemindersByCategory(position);
            List<String> names = new ArrayList<>();
            reminderIDs = new int[reminders.size()];
            int i = 0;
            for (Reminder reminder : reminders) {
                names.add(String.format("%02d", reminder.hour) + ":" + String.format("%02d", reminder.minute) + " " +
                        reminder.name);
                reminderIDs[i++] = reminder.id;
            }
            adapter.clear();
            adapter.addAll(names);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
