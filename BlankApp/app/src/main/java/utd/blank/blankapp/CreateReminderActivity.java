package utd.blank.blankapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.util.List;

public class CreateReminderActivity extends AppCompatActivity {
    private String TAG = "cra";
    private int currentID = -1;
    static int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        int reminder_id = intent.getIntExtra(MainActivity.REMINDER_ID_MESSAGE, -1);
        currentID = reminder_id;
        Log.d(TAG, "Received " + reminder_id);


        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
        Reminder r = dbHelper.getReminder(reminder_id);
        Log.d(TAG, "Retrieved " + r.name + " " + r.id);
        if (reminder_id >= 1) {
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText(r.name);
            editText = (EditText) findViewById(R.id.editText2);
            editText.setText(r.description);
            TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
            timePicker.setCurrentHour(r.hour);
            timePicker.setCurrentMinute(r.minute);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            //imageView.setImageURI(Uri.fromFile(new File(r.imagePath)));
        } else {
            //its a new reminder
            ((Button)findViewById(R.id.button4)).setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveReminder(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        Reminder r = new Reminder();
        r.id = currentID;   //awful idea
        EditText editText = (EditText) findViewById(R.id.editText);
        r.name = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editText2);
        r.description = editText.getText().toString();
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        //the new methods don't work? but deprecated do.
        r.hour = timePicker.getCurrentHour();
        r.minute = timePicker.getCurrentMinute();
        //r.hour = timePicker.getHour();
        //r.minute = timePicker.getMinute();

        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
        Log.d(TAG, String.format("Adding or updating reminder with id %d", currentID));
        if (r.id >= 0) {
            dbHelper.updateReminder(r);
        } else {
            dbHelper.addReminder(r);
        }

        startActivity(intent);
    }

    public void openGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void deleteReminder(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
        dbHelper.deleteReminder(currentID);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            Log.d(TAG, "Got " + imageUri.getPath());
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }


}
