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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.adapters.MDArrayAdapter;
import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

public class SharedMDFragment extends Fragment implements AbsListView.OnItemClickListener {
    /**
     * Static VARS
     */
    private static int loadStep = 10;
    private static final String ARG_USER_TOKEN = "user_token";
    private static final String TAG = "MotionDetectFragment";
    private static ArrayList<MotionDetectNew> mMotionDetectList = null;
    /**
     * VARS
     */
    private String mUserToken;
    private MainActivity mMainActivity;
    private MDArrayAdapter motionDetectAdapter;
    private AbsListView mListView;
    private ProgressDialog pd;
    private int mMdLoadedItems = 0;
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
    public static SharedMDFragment newInstance(String userToken) {
        SharedMDFragment fragment = new SharedMDFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     */
    public SharedMDFragment() {
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

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    getSharedMotionDetectList();
                }
            }
        });

        getSharedMotionDetectList();

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

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }
    public void updateDisplay(){
        Log.d("MyApp", "updateDisplay");
        //**********************
        // Set the adapter
        if(null != mMotionDetectList) {
            if( motionDetectAdapter == null ) {
                motionDetectAdapter = new MDArrayAdapter(this, R.layout.item_motion_detect, mMotionDetectList);
                mListView.setAdapter(motionDetectAdapter);
            }
        }
    }

    /**
     * REST Request for getMotionDetectListByCustomer
     */
    private void getSharedMotionDetectList() {
        pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getSharedMotionDetectList");
        rp.setParam("user_token", mUserToken);
        rp.setParam("start", Integer.toString(mMdLoadedItems));
        rp.setParam("length", Integer.toString(loadStep));
        getSharedMotionDetectListAsyncTask task = new getSharedMotionDetectListAsyncTask();
        task.execute(rp);
    }
    private class getSharedMotionDetectListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            if(s == null)
                return;
            Log.d("MyApp", "getMotionDetectListByCustomer replay" + ": " + s.length());
            JSONObject mdObject;
            try {
                mMdLoadedItems += loadStep;
                mdObject = new JSONObject(s);
                if(mdObject.has("md_list")) {
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
}
