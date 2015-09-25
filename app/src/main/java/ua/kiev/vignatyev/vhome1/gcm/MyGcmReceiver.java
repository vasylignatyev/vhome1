package ua.kiev.vignatyev.vhome1.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vignatyev on 23.09.2015.
 */
public class MyGcmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("MyApp", "MyGcmReceiver action: " + action);
        switch (action) {
            case "com.google.android.c2dm.intent.REGISTRATION":
                String registrationId = intent.getStringExtra("registration_id");
                Log.d("MyApp","MyGcmReceiver::registrationId " + registrationId);
                String error = intent.getStringExtra("error");
                Log.d("MyApp","MyGcmReceiver::error " + error);
                String unregistred = intent.getStringExtra("unregistred");
                Log.d("MyApp","MyGcmReceiver::unregistred " + unregistred);
                break;
            case "com.google.android.c2dm.intent.RECIEVE":
                String data1 = intent.getStringExtra("data1");
                String data2 = intent.getStringExtra("data2");
                break;
        }
    }
}
