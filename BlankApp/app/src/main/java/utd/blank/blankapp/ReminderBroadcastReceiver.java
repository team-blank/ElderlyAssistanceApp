package utd.blank.blankapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by atvaccaro on 11/29/15.
 */
public class ReminderBroadcastReceiver extends BroadcastReceiver {
    public static String TAG = "ReminderBroadcastReceiver";
    public static int NOTIFICATION_ID = 1234;
    public static String NOTIFICATION = "notification";
    public static final int REMINDER_CODE = 1234;
    public static final int SMS_CODE = 2345;
    public static String REMINDER_NAME_MESSAGE = "reminder_name_message";
    public static String REMINDER_DESCRIPTION_MESSAGE = "reminder_description_message";
    public static String REMINDER_IMAGE_MESSAGE = "reminder_image_message";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent i = new Intent(context, ReminderIntentService.class);
//        i.putExtra("foo", "bar");
//        context.startService(i);
        String name = intent.getStringExtra(REMINDER_NAME_MESSAGE);
        String description = intent.getStringExtra(REMINDER_DESCRIPTION_MESSAGE);
        String imagePath = intent.getStringExtra(REMINDER_IMAGE_MESSAGE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //build bitmap
        File imgFile = new  File(imagePath);
        Bitmap bmp = null;

        if(imgFile.exists()){
            bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
        } else {
            Log.d(TAG, "Image file not found");
        }

        Notification n = new Notification.Builder(context)
                .setContentTitle(name)
                .setContentText(description)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.notify)
                .setStyle(new Notification.BigPictureStyle().bigPicture(bmp))
                .build();


        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        Log.d(TAG, "Sending notification w/ name " + name + " and description " + description + " and path " + imagePath);
        notificationManager.notify(NOTIFICATION_ID, n);

        List<Reminder> reminders = ReminderDatabaseHelper.getInstance(context).getAllReminders();
        List<String> names = new ArrayList<>();
        int[] reminderIDs = new int[reminders.size()];
        int i = 0;
        Reminder soonest = null;
        if (reminders.size() > 0) {

            soonest = reminders.get(0);    //for getting next reminder to schedule
            Date currentDate = Calendar.getInstance().getTime();
            for (Reminder reminder : reminders) {
                names.add(reminder.hour + ":" + reminder.minute + " " + reminder.name);
                reminderIDs[i++] = reminder.id;

                if ((reminder.hour < currentDate.getHours() ? reminder.hour + 24 : reminder.hour) - currentDate.getHours()
                        <=
                        (soonest.hour < currentDate.getHours() ? soonest.hour + 24 : soonest.hour) - currentDate.getHours()) {
                    if ((reminder.minute < currentDate.getMinutes() ? reminder.minute + 60 : reminder.minute) - currentDate.getMinutes()
                            <=
                            (soonest.minute < currentDate.getMinutes() ? soonest.minute + 60 : soonest.minute) - currentDate.getMinutes()) {
                        if (reminders.size() > 1 && (reminder.hour != currentDate.getHours() || reminder.minute != currentDate.getMinutes())) { soonest = reminder; }
                    }
                }
            }
        }
        if (soonest != null) {
            Log.d(TAG, "Scheduled " + soonest.name + " for " + soonest.hour + ":" + soonest.minute);
            ReminderBroadcastReceiver.setAlarm(context, soonest);
        }

        Intent smsIntent = new Intent(context, SmsBroadcastReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, ReminderBroadcastReceiver.SMS_CODE,
                smsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30*1000, pIntent);
    }

    public static void setAlarm(Context context, Reminder r) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        //put extras
        intent.putExtra(REMINDER_NAME_MESSAGE, r.name);
        intent.putExtra(REMINDER_DESCRIPTION_MESSAGE, r.description);
        intent.putExtra(REMINDER_IMAGE_MESSAGE, r.imagePath);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, ReminderBroadcastReceiver.REMINDER_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
//                AlarmManager.INTERVAL_HALF_HOUR, pIntent);
        Date currentDate = Calendar.getInstance().getTime();
        int hoursDelay = (r.hour < currentDate.getHours() ? r.hour + 24 : r.hour) - currentDate.getHours();
        int minutesDelay = (r.minute < currentDate.getMinutes() ? r.minute + 60 : r.minute) - currentDate.getMinutes();
        if (r.hour == currentDate.getHours() && r.minute == currentDate.getMinutes()) {
            hoursDelay += 24;
        }
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + hoursDelay*3600*1000 + minutesDelay*60*1000, pIntent);
        Log.d(TAG, "Scheduled alarm w/ delay of " + hoursDelay + ":" + minutesDelay);
    }
}
