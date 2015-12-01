package utd.blank.blankapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsBroadcastReceiver";

    public SmsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        String CONTACT_NUMBER = SP.getString("contactnumber", "NA");

        if (existAlarm(context, ReminderBroadcastReceiver.SMS_CODE) != null) {
            SmsManager smsManager = SmsManager.getDefault();
            Log.d(TAG, "Reminder missed, sending sms to " + CONTACT_NUMBER);
            smsManager.sendTextMessage("+1" + CONTACT_NUMBER, null, "Reminder not answered", null, null);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            Log.d(TAG, "Reminder confirmed, sending sms to " + CONTACT_NUMBER);
            smsManager.sendTextMessage("+1" + CONTACT_NUMBER, null, "Reminder answered", null, null);
        }
    }

    public PendingIntent existAlarm(Context context, int id) {
        Intent intent = new Intent(context, SmsBroadcastReceiver.class);
        intent.setAction(Intent.ACTION_VIEW);
        PendingIntent test = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        return test;
    }
}
