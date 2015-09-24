package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ListAdapter;
import android.widget.TextView;


import ua.kiev.vignatyev.vhome1.adapters.VcamAdapter;
import ua.kiev.vignatyev.vhome1.parsers.VcamParser;

public class ScamFragment extends Fragment implements AbsListView.OnItemClickListener, VcamAdapter.OnAdapterInteractionListener {
    /**
     * STATIC VAR
     */
    private static final String USER_TOKEN = "user_token";
    private static final String TAG = "ScamFragment";
    /**
     * VARS
     */
    private VcamAdapter vcamAdapter;
    private MainActivity mMainActivity;
    private ProgressDialog pd;
    private String mUserToken;
    private AbsListView mListView;

    /**
     *
     */
    public ScamFragment() {
    }

    /**
     *
     * @param userToken
     * @return
     */
    public static ScamFragment newInstance(String userToken) {
        ScamFragment fragment = new ScamFragment();
        Bundle args = new Bundle();
        args.putString(USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        pd = new ProgressDialog(activity);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
        mMainActivity = (MainActivity) activity;

        super.onAttach(activity);
     }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserToken = getArguments().getString(USER_TOKEN);
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
        View view = inflater.inflate(R.layout.fragment_scam_list, container, false);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));;
        mListView.setOnItemClickListener(this);

        return view;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mMainActivity.isScamListEmpty()) {
            getSharedVCamList();
        } else {
            updateDisplay();
        }
    }

    /**
     *
     */
    public void updateDisplay(){
        vcamAdapter = new VcamAdapter(getActivity(), R.layout.item_vcam, mMainActivity.getScamList());
        //**********************
        // Set the listener
        vcamAdapter.setOnAdapterInteractionListener(this);
        //**********************
        // Set the adapter
        if(null != vcamAdapter) {
            mListView.setAdapter(vcamAdapter);
        }
        pd.hide();
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //set arguments
        Fragment newFragment = (Fragment) VcamPlayerFragment.newInstance(new Integer(view.getTag().toString()) );
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, newFragment, VcamPlayerFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentManager.executePendingTransactions();
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
     * @param v
     */
    @Override
    public void onArchButtonClick(View v) {
        Log.d("MyApp", TAG + " onArchButtonClick");

        Log.d("myApp", "Archive vcam token: " + v.getTag());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment newFragment = VarchFragment.newInstance(mUserToken, v.getTag().toString());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, newFragment, ScamFragment.TAG);
        transaction.addToBackStack(TAG);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    /**
     * REST Request for Vcam List
     */
    public void getSharedVCamList() {
        pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getSharedVCamList");
        rp.setParam("customer", mUserToken);

        GetSharedVCamListAsyncTask task = new GetSharedVCamListAsyncTask();
        task.execute(rp);
    }
    /**
     * Async taskfor Vcam List
     */
    public class GetSharedVCamListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            mMainActivity.setScamList(VcamParser.parseFeed(s));
            updateDisplay();
        }
    }
}
