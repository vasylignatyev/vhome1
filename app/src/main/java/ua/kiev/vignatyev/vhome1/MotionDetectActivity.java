package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MotionDetectActivity extends Activity {
    /* VARS */
    private String mUserName, mUserPass, mUserToken, mICustomerVcam;
    //private int ;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detect);

        sp = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        if (savedInstanceState == null) {
            mUserName = sp.getString("userName", null);
            mUserPass = sp.getString("userPass", null);
        } else {
            mUserName = savedInstanceState.getString("userName", null);
            mUserPass = savedInstanceState.getString("userPass", null);
        }
        if ((null != mUserName) && (null != mUserPass)) {
            confirmAuthentication();
        }


        Intent intent = getIntent();
        String iMotionDetect = intent.getStringExtra("i_motion_detect");
        mICustomerVcam = intent.getStringExtra("i_customer_vcam");
        Log.d("MyApp", "MotionDetectActivity i_motion_detect: " + iMotionDetect);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_motion_detect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     *
     */
    private void confirmAuthentication() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "confirmAuthentication");
        rp.setParam("user_email", mUserName);
        rp.setParam("user_pass", mUserPass);
        confirmAuthenticationAsyncTask task = new confirmAuthenticationAsyncTask();
        task.execute(rp);
    }
    public class confirmAuthenticationAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "confirmAuthenticationAsync replay" + ": " + s);
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.has("error")) {
                    Toast toast = Toast.makeText(MotionDetectActivity.this, "Wrong, password!!!", Toast.LENGTH_LONG);
                    toast.show();
                } else if (obj.has("token")) {
                    mUserToken = obj.getString("token");
                    getMotionDetectList(Integer.parseInt(mICustomerVcam));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(MotionDetectActivity.this, "SERVER CONNECTION ERROR!!!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
    /**
     *
     */
    private void getMotionDetectList(int iCustomerVcam) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMotionDetectList");
        rp.setParam("user_token", mUserToken);
        rp.setParam("i_customer_vcam", Integer.toString(iCustomerVcam));
        GetMotionDetectListAsyncTask task = new GetMotionDetectListAsyncTask();
        task.execute(rp);
    }
    public class GetMotionDetectListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "GetMotionDetectListAsyncTask replay" + ": " + s.length());
            try {
                JSONArray obj = new JSONArray(s);
                Log.d("MyApp", "Motion #" + obj.length());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("MyApp", "Json error");
            }
        }
    }
}
