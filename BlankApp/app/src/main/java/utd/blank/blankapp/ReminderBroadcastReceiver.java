package utd.blank.blankapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by atvaccaro on 11/29/15.
 */
public class ReminderBroadcastReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    public static final int REQUEST_CODE = 1234;

    @Override
    public void onReceive(Context context, Intent intent) {
//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(NOTIFICATION, "onReceived called");
//
//        Notification notification = intent.getParcelableExtra(NOTIFICATION);
//        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
//        notificationManager.notify(id, notification);

        Intent i = new Intent(context, ReminderIntentService.class);
        i.putExtra("foo", "bar");
        context.startService(i);

//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
//        wl.acquire();
//        Toast.makeText(context, "test toast", Toast.LENGTH_LONG).show();
//        wl.release();
    }

    public void setAlarm(Context context, Reminder r) {
//        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
//        //intent.putExtra(ONE_TIME, Boolean.FALSE);
//        PendingIntent pi = PendingIntent.getBroadcast(context, r.id, intent, 0);
//        //After after 5 seconds
//        am.set(AlarmManager.RTC_WAKEUP, -1, pi);
//        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 1000 * 5 , pi);
//        Toast.makeText(context, "Set notification", Toast.LENGTH_LONG).show();
//        Log.d(NOTIFICATION, "Set notification");


        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, ReminderBroadcastReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }
}
