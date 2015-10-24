package ua.kiev.vignatyev.vhome1.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import ua.kiev.vignatyev.vhome1.MDActivity;
import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.validator.MDActivityNew;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String motionDate = data.getString("motion_date",null);
        String vcamLocation = data.getString("vcam_location",null);
        String vcamName = data.getString("vcam_name",null);

        Log.d("MyApp", "From: " + from);
        Log.d("MyApp", "Message: " + message);
        if(null != motionDate) Log.d("MyApp", "motionDate: " + motionDate);
        if(null != vcamLocation) Log.d("MyApp", "vcamLocation: " + vcamLocation);
        if(null != vcamName) Log.d("MyApp", "vcamName: " + vcamName);


        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }


        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, data);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, Bundle data) {

        //Intent intent = new Intent(this, MDActivity.class);
        Intent intent = new Intent(this, MDActivityNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtras(data);

        String iCustomerVcam = data.getString("i_customer_vcam", null);

        Log.d("MyApp", "iCustomerVcam = " + iCustomerVcam);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(iCustomerVcam), intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(data.getString("vcam_name", "Детектор движения"))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(iCustomerVcam), notificationBuilder.build());
    }
}
