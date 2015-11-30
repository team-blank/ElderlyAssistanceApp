package utd.blank.blankapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atvaccaro on 11/3/15.
 */
public class ReminderDatabaseHelper extends SQLiteOpenHelper {
    private static ReminderDatabaseHelper sInstance;

    private static String DATABASE_NAME = "reminder_db";
    private static int DATABASE_VERSION = 1;

    private static String TABLE_REMINDERS = "reminders";

    private static String KEY_REMINDER_ID = "id";
    private static String KEY_REMINDER_NAME = "name";
    private static String KEY_REMINDER_DESCRIPTION = "description";
    private static String KEY_REMINDER_CATEGORY = "category";
    private static String KEY_REMINDER_HOUR = "hour";
    private static String KEY_REMINDER_MINUTE = "minute";
    private static String KEY_REMINDER_IMAGEPATH = "imagepath";

    private static String TAG = "db";   //tag output as db

    private ReminderDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS +
                "(" +
                KEY_REMINDER_ID + " INTEGER PRIMARY KEY," +
                KEY_REMINDER_NAME + " TEXT, " +
                KEY_REMINDER_DESCRIPTION + " TEXT, " +
                KEY_REMINDER_CATEGORY + " INTEGER, " +
                KEY_REMINDER_HOUR + " INTEGER, " +
                KEY_REMINDER_MINUTE + " INTEGER, " +
                KEY_REMINDER_IMAGEPATH + " TEXT" +
                ")";

        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
            onCreate(db);
        }
    }

    public static synchronized ReminderDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new ReminderDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void addReminder(Reminder reminder) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            //build values
            ContentValues values = new ContentValues();
            values.put(KEY_REMINDER_NAME, reminder.name);
            values.put(KEY_REMINDER_DESCRIPTION, reminder.description);
            values.put(KEY_REMINDER_CATEGORY, reminder.category);
            values.put(KEY_REMINDER_HOUR, reminder.hour);
            values.put(KEY_REMINDER_MINUTE, reminder.minute);
            values.put(KEY_REMINDER_IMAGEPATH, reminder.imagePath);

            //try to insert; sqlite auto increments the primary key column on an insert so don't need to specify
            db.insertOrThrow(TABLE_REMINDERS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add reminder to database");
        } finally {
            db.endTransaction();
        }
    }   //end addReminder

    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG, String.format("Updating reminder with id %d", reminder.id));

        ContentValues values = new ContentValues();
        values.put(KEY_REMINDER_MINUTE, reminder.minute);
        values.put(KEY_REMINDER_HOUR, reminder.hour);
        values.put(KEY_REMINDER_DESCRIPTION, reminder.description);
        values.put(KEY_REMINDER_CATEGORY, reminder.category);
        values.put(KEY_REMINDER_NAME, reminder.name);
        values.put(KEY_REMINDER_IMAGEPATH, reminder.imagePath);

        //do need an id when updating, though
        return db.update(TABLE_REMINDERS,
                values,
                KEY_REMINDER_ID + " = ?",
                new String[] { String.format("%d", reminder.id) }
        );
    }   //end updateReminderHour

    public Reminder getReminder(int id) {
        String REMINDER_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s;",
                TABLE_REMINDERS, KEY_REMINDER_ID, id);
        Log.d(TAG, REMINDER_SELECT_QUERY);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(REMINDER_SELECT_QUERY, null);
        Log.d(TAG, String.format("Rows: %d", cursor.getCount()));
        Reminder reminder = new Reminder();
        try {
            if (cursor.moveToFirst()) {
                reminder.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_ID)));
                reminder.name = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_NAME));
                reminder.description = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_DESCRIPTION));
                reminder.category = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_CATEGORY)));
                reminder.hour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_HOUR)));
                reminder.minute = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_MINUTE)));
                reminder.imagePath = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_IMAGEPATH));
                Log.d(TAG, String.format("%s %s %s %s %s %s %s",
                        reminder.id, reminder.name, reminder.description,
                        reminder.category, reminder.hour, reminder.minute, reminder.imagePath));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to retrieve reminder");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return reminder;
    }

    public List<Reminder> getRemindersByCategory(int category) {
        List<Reminder> reminders = new ArrayList<>();
        // SELECT * FROM REMINDERS
        String REMINDERS_SELECT_QUERY = String.format("SELECT * FROM %s " +
                        "WHERE %s = %d " +
                        "ORDER BY %s, %s;",
                TABLE_REMINDERS, KEY_REMINDER_CATEGORY, category, KEY_REMINDER_HOUR, KEY_REMINDER_MINUTE);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(REMINDERS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Reminder newReminder = new Reminder();
                    newReminder.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_ID)));
                    newReminder.name = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_NAME));
                    newReminder.description = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_DESCRIPTION));
                    newReminder.category = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_CATEGORY)));
                    newReminder.hour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_HOUR)));
                    newReminder.minute = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_MINUTE)));
                    newReminder.imagePath = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_IMAGEPATH));
                    reminders.add(newReminder);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get reminders from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return reminders;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();

        // SELECT * FROM REMINDERS
        String REMINDERS_SELECT_QUERY = String.format("SELECT * FROM %s ORDER BY %s, %s;", TABLE_REMINDERS, KEY_REMINDER_HOUR, KEY_REMINDER_MINUTE);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(REMINDERS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Reminder newReminder = new Reminder();
                    newReminder.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_ID)));
                    newReminder.name = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_NAME));
                    newReminder.description = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_DESCRIPTION));
                    newReminder.category = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_CATEGORY)));
                    newReminder.hour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_HOUR)));
                    newReminder.minute = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_MINUTE)));
                    newReminder.imagePath = cursor.getString(cursor.getColumnIndex(KEY_REMINDER_IMAGEPATH));
                    reminders.add(newReminder);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get reminders from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return reminders;
    }   //end getAllReminders

    public void deleteReminder(int id) {
        String table = TABLE_REMINDERS;
        String whereClause = "id = " + id;
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, whereClause, null);
    }

    //don't use this
    public void deleteAllReminders() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_REMINDERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all reminders");
        } finally {
            db.endTransaction();
        }
    }
}
