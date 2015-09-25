package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ua.kiev.vignatyev.vhome1.adapters.MotionDetectAdapter;
import ua.kiev.vignatyev.vhome1.models.MotionDetect;
import ua.kiev.vignatyev.vhome1.models.MotionDetectEvent;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectEventParser;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MotionDetectFragment extends Fragment implements AbsListView.OnItemClickListener {
    /**
     * Static VARS
     */
    private static final String ARG_USER_TOKEN = "user_token";
    private static final String TAG = "MotionDetectFragment";
    private static ArrayList<MotionDetect> mMotionDetectList = null;
    private static int counter = 0;
    /**
     * VARS
     */
    private String mUserToken;
    private MainActivity mMainActivity;
    private MotionDetectAdapter motionDetectAdapter;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Button btnDate;
    private AbsListView mListView;

    /**
     * GETTERS & SETTERS
     */
    public static ArrayList<MotionDetect> getMotionDetectList() {
        return mMotionDetectList;
    }
    public static void setMotionDetectList(ArrayList<MotionDetect> motionDetectList) {
        MotionDetectFragment.mMotionDetectList = motionDetectList;
    }
    public static MotionDetect getMotionDetect(int position) {
        if(null == mMotionDetectList )
            return null;
        return mMotionDetectList.get(position);
    }

    /**
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
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
        Calendar calender = Calendar.getInstance();
        mYear = calender.get(Calendar.YEAR);
        mMonth = calender.get(Calendar.MONTH);
        mDay = calender.get(Calendar.DAY_OF_MONTH);
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

        getNotificationTable();
        btnDate = (Button) view.findViewById(R.id.btDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                        setDate();
                        getNotificationTable();
                    }
                };
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance( mYear, mMonth, mDay);
                datePickerFragment.setCallBack(onDateSetListener);
                datePickerFragment.show(mMainActivity.getFragmentManager(), "TEST");
            }
        });
        setDate();


        return view;
    }
    /**
     *
     */
    public void setDate(){
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay, 0, 0, 0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        btnDate.setText(df.format(c.getTime()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("MyApp", "Click Motion Detact Item: " + view.getTag().toString());
        //genMotionDetectPopup(view.getTag().toString());
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }
    public void updateDisplay(){
        Log.d("MyApp", "updateDisplay");
        motionDetectAdapter = new MotionDetectAdapter(getActivity(), R.layout.item_motion_detect, mMotionDetectList);
        //**********************
        // Set the adapter
        mListView.setAdapter(motionDetectAdapter);
    }

    /**
     * REST Request for getNotification Table
     */
    private void getNotificationTable() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay, 0, 0, 0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String cDate = df.format(c.getTime());

        Log.d("MyApp", "getNotificationTable token: " + mUserToken);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.getServerUrl() + "/php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getNotificationTable");
        rp.setParam("token", mUserToken);
        rp.setParam("draw", "1");
        //rp.setParam("fromDate", cDate + " 00:00:00" );
        rp.setParam("fromDate", "2015-01-01 00:00:00" );
        rp.setParam("tillDate", cDate + " 23:59:59" );

        getNotificationTableAsyncTask task = new getNotificationTableAsyncTask();
        task.execute(rp);
    }

    /**
     * Async taskfor getNotification Table
     */
    private class getNotificationTableAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            mMotionDetectList = MotionDetectParser.parseFeed(s);
            if(null != mMotionDetectList) {
                for (MotionDetect motionDetect : mMotionDetectList) {
                    genMotionDetectPopup(motionDetect);
                }

            } else {
                Log.d("MyApp","getSharedNotificationTable parser error: " +s);
            }
        }
    }
    /**
     * REST Request for genMotionDetect Popup
     */
    private void genMotionDetectPopup(MotionDetect motionDetect) {

        String motionDetectId = Integer.toString(motionDetect.iMotionDetect);

        Log.d("MyApp", "genMotionDetectPopup iMotionDetect: " + motionDetectId);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/genMotionDetectPopup.php");
        rp.setMethod("GET");
        rp.setParam("token", mUserToken);
        rp.setParam("id", motionDetectId);

        genMotionDetectPopupAsyncTask task = new genMotionDetectPopupAsyncTask();
        task.execute(rp);
        counter++;
    }

    /**
     * Async taskfor getNotification Table
     */
    private class genMotionDetectPopupAsyncTask extends AsyncTask<RequestPackage, Void, String> {

        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {

            MotionDetectEvent motionDetectEvent = MotionDetectEventParser.parseFeed(s);
            if( null != motionDetectEvent) {
                MotionDetect motionDetect = new MotionDetect(motionDetectEvent.iMotionDetect);
                int i = mMotionDetectList.indexOf(motionDetect);
                if( i >= 0 ) {
                    mMotionDetectList.get(i).setEventParams(motionDetectEvent);
                } else {
                    Log.d("MyApp", "Error index is: " + i);
                }
                //Log.d("MyApp", "genMotionDetectPopupAsyncTask Replay: " + motionDetectEvent.toString());
            }
            if(--counter == 0)
                updateDisplay();
        }
    }

}
