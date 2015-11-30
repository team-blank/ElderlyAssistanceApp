package utd.blank.blankapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * IntentService for notifications
 */
public class ReminderIntentService extends IntentService {
    public static int NOTIFICATION_ID = 1;
    public static String NOTIFICATION = "notification";
    public static String TAG = "ReminderIntentService";

    public ReminderIntentService() {
        super("ReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent called");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        nBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, nBuilder.build());

        SmsManager smsManager = SmsManager.getDefault();
        Log.d(TAG, "sending sms");
        smsManager.sendTextMessage("+15406415112", null, "Reminder not answered", null, null);
    }
}
