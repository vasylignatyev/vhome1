package ua.kiev.vignatyev.vhome1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

public class MotionDetectActivity extends FragmentActivity {

    private static final String I_CUSTOMER_VCAM = "i_customer_vcam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detect);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String i_customer_vcam = extras.getString(I_CUSTOMER_VCAM, null);
            if(null != i_customer_vcam) {
                Log.d("MyApp", "MDActivity I_CUSTOMER_VCAM: " + i_customer_vcam);
                Bundle args = new Bundle();
                args.putInt(I_CUSTOMER_VCAM, Integer.parseInt(i_customer_vcam));

                FragmentManager fragmentManager = getSupportFragmentManager();
                MDFragment mdFragment = new MDFragment();
                mdFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.fragment_md, mdFragment).commit();
            }
        }



    }

}
