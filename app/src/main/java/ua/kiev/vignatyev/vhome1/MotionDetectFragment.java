package ua.kiev.vignatyev.vhome1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ua.kiev.vignatyev.vhome1.adapters.MDArrayAdapter;
import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

public class MotionDetectFragment extends Fragment
        implements AbsListView.OnItemClickListener, MDArrayAdapter.OnMDArrayAdapterListener {
    /**
     * Static VARS
     */
    public static final String ARG_USER_TOKEN = "user_token";
    public static final String ARG_OFFSET = "offset";
    public static final String ARG_I_CUSTOMER_VCAM = "i_customer_vcam";

    private static int loadStep = 10;
    private static final String TAG = "MotionDetectFragment";
    private static ArrayList<MotionDetectNew> mMotionDetectList = null;

    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
    /**
     * VARS
     */
    private Context mContext;
    private String mUserToken;
    private int mICustomerVcam;
    private int mOffset;
    //private MainActivity mMainActivity;
    private MDArrayAdapter motionDetectAdapter;
    private AbsListView mListView;
    private ProgressDialog pd;
    private int mMdLoadedItems = 0;
    /**
     * GETTERS & SETTERS
     */

    /**
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
    }

    /**
     * @param userToken
     * @return
     */
    public static MotionDetectFragment newInstance(String userToken, int offset) {
        MotionDetectFragment fragment = new MotionDetectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TOKEN, userToken);
        args.putInt(ARG_OFFSET, offset);
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
            mUserToken = getArguments().getString(ARG_USER_TOKEN, null);
            mICustomerVcam = getArguments().getInt(ARG_I_CUSTOMER_VCAM, 0);
            mOffset = getArguments().getInt(ARG_OFFSET, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_motion_detect, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);

        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    getMotionDetectList();
                }
            }
        });

        getMotionDetectList();

        return view;
    }

    @Override
    public void onDestroyView() {
        ((ArrayAdapter) mListView.getAdapter()).clear();
        mMotionDetectList = null;
        super.onDestroyView();
        Log.d("MyApp", "MotionDetectFragment::onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("MyApp", "Click Motion Detact Item: " + view.getTag().toString());
    }

    public void updateDisplay() {
        Log.d("MyApp", "updateDisplay");
        //**********************
        // Set the adapter
        if (null != mMotionDetectList) {
            if (motionDetectAdapter == null) {
                //motionDetectAdapter = new MDArrayAdapter(getActivity(), R.layout.item_motion_detect, mMotionDetectList);
                motionDetectAdapter = new MDArrayAdapter( this , R.layout.item_motion_detect, mMotionDetectList);
                mListView.setAdapter(motionDetectAdapter);
            }
        }
    }

    /**
     * REST Request for getMotionDetectList
     */
    private void getMotionDetectList() {
        pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMotionDetectList");
        if(mUserToken != null)
            rp.setParam("user_token", mUserToken);
        if(mICustomerVcam != 0 )
            rp.setParam("i_customer_vcam", Integer.toString(mICustomerVcam));
        rp.setParam("start", Integer.toString(mMdLoadedItems));
        rp.setParam("length", Integer.toString(loadStep));
        getMotionDetectListAsyncTask task = new getMotionDetectListAsyncTask();
        task.execute(rp);
    }
    private class getMotionDetectListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null)
                return;
            //Log.d("MyApp", "getMotionDetectList replay" + ": " + s.length());
            JSONObject mdObject;
            try {
                mMdLoadedItems += loadStep;
                mdObject = new JSONObject(s);
                if (mdObject.has("md_list")) {
                    JSONArray mdArray = mdObject.getJSONArray("md_list");
                    mMotionDetectList = MotionDetectParserNew.parseFeed(mMotionDetectList, mdArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                pd.hide();
                updateDisplay();
            }
        }
    }

    /**
     * REST Request for genMotionDetectPopup
     */

    @Override
    public void genMotionDetectPopup(int iMotionDetect) {
        //pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "genMotionDetectPopup");
        rp.setParam("token", mUserToken);
        rp.setParam("id", Integer.toString(iMotionDetect));
        genMotionDetectPopupAsyncTask task = new genMotionDetectPopupAsyncTask();
        task.execute(rp);
    }

    private class genMotionDetectPopupAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            if(s == null)
                return;
            Log.d("MyApp", "genMotionDetectPopup replay: " + s);

            JSONObject obj, text;
            int iMotionDetect;
            String camToken,eventDate;

            try {
                obj = new JSONObject(s);
                if(obj.has("error")) {
                    Toast.makeText(mContext, "Видео доступно только из приложения", Toast.LENGTH_LONG).show();
                }
                if(obj.has("text")){
                    text = obj.getJSONObject("text");
                    iMotionDetect = text.getInt("I_MOTION_DETECT");
                    eventDate = text.getString("EVENT_DATE");
                    camToken = text.getString("CAM_TOKEN");

                    Intent intent = new Intent(mContext, VarchPlayerActivity.class);
                    intent.putExtra(VarchPlayerFragment.ARG_I_MOTION_DETECT, iMotionDetect);
                    intent.putExtra(VarchPlayerFragment.ARG_ISSUE_DATE, eventDate);
                    intent.putExtra(VarchPlayerFragment.ARG_USER_TOKEN, mUserToken);
                    intent.putExtra(VarchPlayerFragment.ARG_VCAM_TOKEN, camToken);
                    mContext.startActivity(intent);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
