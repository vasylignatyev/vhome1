package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.adapters.MotionDetectAdapterNew;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

public class MotionDetectFragment extends Fragment implements AbsListView.OnItemClickListener {
    /**
     * Static VARS
     */
    private static final String ARG_USER_TOKEN = "user_token";
    private static final String TAG = "MotionDetectFragment";
    private static ArrayList<MotionDetectNew> mMotionDetectList = null;
    /**
     * VARS
     */
    private String mUserToken;
    private MainActivity mMainActivity;
    private MotionDetectAdapterNew motionDetectAdapter;
    private AbsListView mListView;
    private ProgressDialog pd;
    /**
     * GETTERS & SETTERS
     */

    /**
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
    }

    /**
     *
     * @param userToken
     * @return
     */
    public static MotionDetectFragment newInstance(String userToken) {
        MotionDetectFragment fragment = new MotionDetectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     */
    public MotionDetectFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserToken = getArguments().getString(ARG_USER_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);

        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        getMotionDetectListByCustomer();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("MyApp", "Click Motion Detact Item: " + view.getTag().toString());
    }

    public void updateDisplay(){
        Log.d("MyApp", "updateDisplay");
        motionDetectAdapter = new MotionDetectAdapterNew(getActivity(), R.layout.item_motion_detect, mMotionDetectList);
        //**********************
        // Set the adapter
        if(null != mMotionDetectList) {
            mListView.setAdapter(motionDetectAdapter);
        }
    }

    /**
     * REST Request for getMotionDetectListByCustomer
     */
    private void getMotionDetectListByCustomer() {
        pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMotionDetectListByCustomer");
        rp.setParam("user_token", mUserToken);
        getMotionDetectListByCustomerAsyncTask task = new getMotionDetectListByCustomerAsyncTask();
        task.execute(rp);
    }
    private class getMotionDetectListByCustomerAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getMotionDetectListByCustomer replay" + ": " + s.length());
            JSONObject mdObject = null;
            try {
                mdObject = new JSONObject(s);

                if(mdObject.has("cam_name")) {
                    //mCamName = mdObject.getString("cam_name");
                    //tvCamName.setText(mCamName);
                }
                if(mdObject.has("cam_token")) {
                    //mCamToken = mdObject.getString("cam_token");
                }
                if(mdObject.has("cam_Location")) {
                    //mCamLocation = mdObject.getString("cam_Location");

                }
                if(mdObject.has("amount")) {
                    //mMDAmount = mdObject.getInt("amount");
                    //tvMDAmount.setText( Integer.toString(mMDAmount) );
                }
                if(mdObject.has("md_list")) {
                    JSONArray mdArray = mdObject.getJSONArray("md_list");
                    mMotionDetectList = MotionDetectParserNew.parseFeed(mdArray);
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
