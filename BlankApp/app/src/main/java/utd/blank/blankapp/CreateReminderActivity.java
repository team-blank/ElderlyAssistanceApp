package utd.blank.blankapp;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class CreateReminderActivity extends AppCompatActivity {
    private String TAG = "CreateReminderActivity";
    private int currentID = -1;
    static int PICK_IMAGE = 100;
    private ReminderBroadcastReceiver notificationManager;
    public static String[] categories = new String[]{
            "Event",
            "Medicine",
            "Food",
            "Pet"
    };
    static String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        s.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentPath != null) {
            Log.d(TAG, currentPath);
        }
        notificationManager = new ReminderBroadcastReceiver();
        Intent intent = getIntent();
        int reminder_id = intent.getIntExtra(MainActivity.REMINDER_ID_MESSAGE, -1);
        currentID = reminder_id;
        Log.d(TAG, "Received " + reminder_id);


        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
        Reminder r = dbHelper.getReminder(reminder_id);
        if (currentPath == null) {
            currentPath = r.imagePath;
        }
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
            Spinner s = (Spinner) findViewById(R.id.spinner);
            s.setSelection(r.category);
            Toast.makeText(this.getApplicationContext(), r.imagePath, Toast.LENGTH_LONG);
            if (currentPath != null) {
                try {
                    imageView.setImageURI(Uri.fromFile(new File(r.imagePath)));
                } catch (Exception e) {

                }
            }

        } else {
            //its a new reminder
            ((Button) findViewById(R.id.button4)).setVisibility(View.INVISIBLE);
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

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Log.d(TAG, "Spinner at " + spinner.getSelectedItemPosition());
        r.category = spinner.getSelectedItemPosition();

        //the new methods don't work? but deprecated do.
        r.hour = timePicker.getCurrentHour();

        r.minute = timePicker.getCurrentMinute();
        //r.hour = timePicker.getHour();
        //r.minute = timePicker.getMinute();

        r.imagePath = currentPath;

        ReminderDatabaseHelper dbHelper = ReminderDatabaseHelper.getInstance(this);
        Log.d(TAG, String.format("Adding or updating reminder with id %d", currentID));
        Log.d(TAG, "r.imagePath:" + r.imagePath + " currentPath:" + currentPath);
        Log.d(TAG, "At time " + r.hour + ":" + r.minute);
        Toast.makeText(getApplicationContext(),
                "Category " + categories[r.category], Toast.LENGTH_LONG)
                .show();
        if (r.id >= 0) {
            dbHelper.updateReminder(r);
        } else {
            dbHelper.addReminder(r);
        }

        //schedule next notification of this reminder
        //not anymore!
//        notificationManager.setAlarm(this.getApplicationContext(), r);


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

            //getting the actual path
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(this.getApplicationContext(), imageUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            currentPath = result;
//            currentPath = imageUri.getPath();

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
            Log.d(TAG, "Set new image?");
        }
    }


}
