package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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

public class VarchFragment extends Fragment
        implements AbsListView.OnItemClickListener, View.OnClickListener {
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
    }

     @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        getCustomerVArchTable();
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
        Fragment newFragment = VarchPlayerFragment.newInstance(varch.archiveName, mUserToken, mVcamToken);
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
    }

    /**
     *
     */
     public interface OnVarchInteractionListener {
        void onVarchInteractionListener(String id);
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
     * REST Request for Varch List
     */
    public void getCustomerVArchTable() {
        Log.d("MyApp", "getCustomerVArchTable token: " + mUserToken);

        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCustomerVArchTable");
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("start", "0");
        rp.setParam("length", "300");
        rp.setParam("draw", "1");

        getCustomerVArchTableAsyncTask task = new getCustomerVArchTableAsyncTask();
        task.execute(rp);
    }

    /**
     * Async taskfor Varch List
     */
    public class getCustomerVArchTableAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","getCustomerVArchTable Replay: " + s);
            mVarchList = VarchParser.parseFeed(s);
            updateDisplay();
        }
    }
}
