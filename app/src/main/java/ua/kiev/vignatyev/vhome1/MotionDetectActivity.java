package ua.kiev.vignatyev.vhome1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class MotionDetectActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detect);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String i_customer_vcam = extras.getString(MotionDetectFragment.ARG_I_CUSTOMER_VCAM, null);
            if(null != i_customer_vcam) {
                Log.d("MyApp", "MotionDetectActivity I_CUSTOMER_VCAM: " + i_customer_vcam);
                Bundle args = new Bundle();
                args.putInt(MotionDetectFragment.ARG_I_CUSTOMER_VCAM, Integer.parseInt(i_customer_vcam));

                FragmentManager fragmentManager = getSupportFragmentManager();
                MotionDetectFragment motionDetectFragment = new MotionDetectFragment();
                motionDetectFragment.setArguments(args);
                //motionDetectFragment.setArguments(extras);
                fragmentManager.beginTransaction().replace(R.id.fragment_md, motionDetectFragment).commit();
            }
        }
    }
}
