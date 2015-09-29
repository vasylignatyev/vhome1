package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.adapters.MotionDetectAdapterNew;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

public class MotionDetectActivity extends Activity {
    /* VARS */
    private String mUserName, mUserPass, mUserToken, mICustomerVcam;
    private String mCamName, mCamToken, mCamLocation;
    private int mMDAmount;
    private ProgressDialog pd;

    private SharedPreferences sp;
    private ArrayList<MotionDetectNew> mMotionList;

    private TextView tvCamName, tvCamLocation, tvMDAmount;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detect);

        pd = new ProgressDialog(this);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");


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
            pd.show();
        }


        Intent intent = getIntent();
        String iMotionDetect = intent.getStringExtra("i_motion_detect");
        mICustomerVcam = intent.getStringExtra("i_customer_vcam");
        Log.d("MyApp", "MotionDetectActivity i_motion_detect: " + iMotionDetect);

        tvCamName = (TextView)findViewById(R.id.tvCamName);
        tvMDAmount = (TextView)findViewById(R.id.tvMDAmount);
        mListView = (ListView)findViewById(R.id.lvMotionDetect);


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

    private void updateDisplay() {
        Log.d("MyApp", "updateDisplay");
        MotionDetectAdapterNew motionDetectAdapter = new MotionDetectAdapterNew(this, R.layout.item_motion_detect, mMotionList);
        //**********************
        // Set the adapter
        mListView.setAdapter(motionDetectAdapter);



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
            JSONObject mdObject = null;
            try {
                mdObject = new JSONObject(s);

                if(mdObject.has("cam_name")) {
                    mCamName = mdObject.getString("cam_name");
                    tvCamName.setText(mCamName);
                }
                if(mdObject.has("cam_token")) {
                    mCamToken = mdObject.getString("cam_token");
                }
                if(mdObject.has("cam_Location")) {
                    mCamLocation = mdObject.getString("cam_Location");

                }
                if(mdObject.has("amount")) {
                    mMDAmount = mdObject.getInt("amount");
                    tvMDAmount.setText( Integer.toString(mMDAmount) );
                }
                if(mdObject.has("md_list")) {
                    JSONArray mdArray = mdObject.getJSONArray("md_list");
                    mMotionList = MotionDetectParserNew.parseFeed(mdArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                pd.hide();
                updateDisplay();
            }

         }
    }
}
