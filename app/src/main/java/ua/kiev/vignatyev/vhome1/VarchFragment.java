package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Calendar;
import java.util.List;

import ua.kiev.vignatyev.vhome1.adapters.VarchArrayAdapter;
import ua.kiev.vignatyev.vhome1.models.Varch;
import ua.kiev.vignatyev.vhome1.parsers.VarchParser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class VarchFragment extends Fragment implements AbsListView.OnItemClickListener, View.OnClickListener {
    /**
     * Static VARS
     */
    private static final String TAG = "VarchFragment";
    private static final String ARG_VCAM_TOKEN = "vcam_token";
    private static final String ARG_USER_TOKEN = "user_token";
    /**
     * VARS
     */
    private Button btnDate;
    private String mVcamToken, mUserToken;
    private MainActivity mMainActivity;
    private int mYear;
    private int mMonth;
    private int mDay;
    private List<Varch> mVarchList;
    private VarchArrayAdapter mVarchArrayAdapter;
    private AbsListView mListView;

    /**
     *
     * @param userToken
     * @param vcamToken
     * @return
     */
    public static VarchFragment newInstance(String userToken, String vcamToken) {
        VarchFragment fragment = new VarchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TOKEN, userToken);
        args.putString(ARG_VCAM_TOKEN, vcamToken);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     */
    public VarchFragment() {
        Calendar calender = Calendar.getInstance();
        mYear = calender.get(Calendar.YEAR);
        mMonth = calender.get(Calendar.MONTH);
        mDay = calender.get(Calendar.DAY_OF_MONTH);
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
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVcamToken = getArguments().getString(ARG_VCAM_TOKEN);
            mUserToken = getArguments().getString(ARG_USER_TOKEN);
         }
     }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_varch, container, false);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        btnDate = (Button) view.findViewById(R.id.btDate);
        btnDate.setOnClickListener(this);
        setDate();
        getCustomerVArchList();
        return view;
    }

    /**
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Varch varch = (Varch) view.getTag();
        Log.d("MyApp","Varch click position: "+ varch.archiveName);
        //varchURL(varch.archiveName);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment newFragment = (Fragment) VarchPlayerFragment.newInstance(varch.archiveName, mUserToken, mVcamToken);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentManager.executePendingTransactions();

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        Log.d("MyApp", "backStackEntryCount: " + new Integer(backStackEntryCount).toString());

    }

    /**
     *
     * @param emptyText
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        Log.d("MyApp", "Click");
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                setDate();
                getCustomerVArchList();
            }
        };
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance( mYear, mMonth, mDay);
        datePickerFragment.setCallBack(onDateSetListener);
        datePickerFragment.show(mMainActivity.getFragmentManager(), "TEST");
    }

    /**
     *
     */
     public interface OnVarchInteractionListener {
        public void onVarchInteractionListener(String id);
    }

    /**
     *
     */
    public void updateDisplay(){
        mVarchArrayAdapter = new VarchArrayAdapter(getActivity(), R.layout.item_varch, mVarchList);
        //**********************
        // Set the adapter
        mListView.setAdapter(mVarchArrayAdapter);
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

    /**
     * REST Request for Varch List
     */
    public void getCustomerVArchList() {
        Log.d("MyApp", "getCustomerVArchList token: " + mUserToken);
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay, 0, 0, 0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String cDate = df.format(c.getTime());

        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCustomerVArchList");
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("start", "0");
        rp.setParam("length", "300");
        rp.setParam("draw", "1");
        rp.setParam("fromDate", cDate + " 00:00:00" );
        rp.setParam("tillDate", cDate + " 23:59:59" );

        getCustomerVArchListAsyncTask task = new getCustomerVArchListAsyncTask();
        task.execute(rp);
    }

    /**
     * Async taskfor Varch List
     */
    public class getCustomerVArchListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","getCustomerVArchList Replay: " + s);
            mVarchList = VarchParser.parseFeed(s);
            updateDisplay();
        }
    }

    /**
     * REST Request for Varch URL
     */
    public void varchURL(String varchName) {
        Log.d("MyApp", "vArchURL name : " + varchName);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "varchURL");
        rp.setParam("user_token", mUserToken);
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("varch_name", varchName);

        GetVarchURLAsyncTask task = new GetVarchURLAsyncTask();
        task.execute(rp);
    }

    /**
     * Async taskfor Varch URL
     */
    public class GetVarchURLAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "GetVarchURLAsyncTask Replay: " + s);
        }
    }
}
