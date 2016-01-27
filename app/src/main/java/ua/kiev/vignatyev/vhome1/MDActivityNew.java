package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.HTTPManager;
import ua.kiev.vignatyev.vhome1.MainActivity;
import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.RequestPackage;
import ua.kiev.vignatyev.vhome1.adapters.MDArrayAdapter;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

public class MDActivityNew extends Activity implements AbsListView.OnItemClickListener {
    /**
     * Static VARS
     */
    private static final String USER_TOKEN = "user_token";
    private static final String I_MOTION_DETECT = "i_motion_detect";
    private static final String I_CUSTOMER_VCAM = "i_customer_vcam";
    private static final String VCAM_LOCATION = "vcam_location";
    private static final String VCAM_NAME = "vcam_name";
    private static final String TOTAL_MD = "total_md";
    private static final String UNREVIEWED_MD = "unreviewed_md";
    /**
     * VARS
     */
    private AbsListView mListView;
    private ProgressDialog pd;
    private String mIMotionDetect;
    //private String mIMotionDetect;
    private static ArrayList<MotionDetectNew> mMotionDetectList = null;
    private MDArrayAdapter motionDetectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdactivity);
        mListView = (AbsListView) findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);

        mListView.setEmptyView(findViewById(android.R.id.empty));

        pd = new ProgressDialog(this);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String i_customer_vcam = extras.getString(I_CUSTOMER_VCAM, null);
            if(null != i_customer_vcam) {
                Log.d("MyApp", "MDActivity I_CUSTOMER_VCAM: " + i_customer_vcam);
                getMotionDetectListByICustomerVcam(Integer.parseInt(i_customer_vcam));
            }
        }

    }

    @Override
    protected void onDestroy() {
        ArrayAdapter arrayAdapter = ((ArrayAdapter)mListView.getAdapter());
        if(null != arrayAdapter) {
            arrayAdapter.clear();
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    public void updateDisplay(){
        Log.d("MyApp", "updateDisplay");

        //**********************
        // Set the adapter
        if(null != mMotionDetectList) {
            //motionDetectAdapter = new MDArrayAdapter(this, R.layout.item_motion_detect, mMotionDetectList);
            mListView.setAdapter(motionDetectAdapter);
        }
    }



    /**
     * REST Request for getMotionDetectListByCustomer
     */
    private void getMotionDetectListByICustomerVcam(int iCustomerVcam) {
        pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMotionDetectList");
        rp.setParam("i_customer_vcam", Integer.toString(iCustomerVcam) );
        getMotionDetectListByICustomerVcamAsyncTask task = new getMotionDetectListByICustomerVcamAsyncTask();
        task.execute(rp);
    }
    private class getMotionDetectListByICustomerVcamAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getMotionDetectListByICustomerVcam replay" + ": " + s.length());
            JSONObject mdObject = null;
            try {
                mdObject = new JSONObject(s);

                if(mdObject.has("cam_name")) {
                    //mCamName = mdObject.getString("cam_name");
                    //tvCamName.setText(mCamName);
                }
                if(mdObject.has("md_list")) {
                    JSONArray mdArray = mdObject.getJSONArray("md_list");
                    mMotionDetectList = MotionDetectParserNew.parseFeed(mMotionDetectList , mdArray);
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
