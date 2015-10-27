package ua.kiev.vignatyev.vhome1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class PinchActivity extends FragmentActivity {
    /**
     * STATIC VARS
     */
    public static final String ARG_IMAGE_URL = "image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uri = extras.getString(ARG_IMAGE_URL, null);
            if(null != uri) {
                Log.d("MyApp", "PinchActivity " + PinchActivity.ARG_IMAGE_URL + ": " + uri);

                FragmentManager fragmentManager = getSupportFragmentManager();

                PinchFragment pinchFragment = PinchFragment.newInstance(uri);
                fragmentManager.beginTransaction().replace(R.id.fragment, pinchFragment).commit();
                //getMD_URL_List();
            }
        }
    }

}
