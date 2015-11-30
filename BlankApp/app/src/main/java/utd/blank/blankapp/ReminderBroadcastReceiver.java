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

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(NOTIFICATION, "onReceived called");

        //Notification notification = intent.getParcelableExtra(NOTIFICATION);
        //int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        //notificationManager.notify(id, notification);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        wl.acquire();
        Toast.makeText(context, "test toast", Toast.LENGTH_LONG).show();
        wl.release();
    }

    public void setAlarm(Context context, Reminder r) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        //intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, r.id, intent, 0);
        //After after 5 seconds
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()-10000, pi);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 1000 * 5 , pi);
        Log.d(NOTIFICATION, "Set notification");
    }
}
