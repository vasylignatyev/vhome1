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
import android.widget.ListView;

import ua.kiev.vignatyev.vhome1.adapters.VcamArrayAdapter;
import ua.kiev.vignatyev.vhome1.parsers.VcamParser;

public class VcamFragment extends Fragment implements AbsListView.OnItemClickListener, VcamArrayAdapter.OnAdapterInteractionListener {
    /**
     * STATIC VAR
     */
    private static final String USER_TOKEN = "user_token";
    private static final String TAG = "VcamFragment";
    /**
     * VARS
     */
    private VcamArrayAdapter vcamArrayAdapter;
    private MainActivity mMainActivity;
    private ProgressDialog pd;
    private String mUserToken;
    private ListView mListView;

    /**
     *
     */
    public VcamFragment() {
    }

    public static VcamFragment newInstance(String userToken) {
        VcamFragment fragment = new VcamFragment();
        Bundle args = new Bundle();
        args.putString(USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        pd = new ProgressDialog(activity);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
        mMainActivity = (MainActivity) activity;

        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserToken = getArguments().getString(USER_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vcam_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(MainActivity.isVcamListEmpty()) {
            getCustomerVCamList();
        } else  {
            updateDisplay();
        }
    }

    public void updateDisplay(){
        vcamArrayAdapter = new VcamArrayAdapter(getActivity(), R.layout.item_vcam, MainActivity.getVcamList());
        if(null != vcamArrayAdapter) {
            vcamArrayAdapter.setOnAdapterInteractionListener(this);
            if(null != mListView ) {
                mListView.setAdapter(vcamArrayAdapter);
            }
        }
        pd.hide();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //set arguments
        String steamURL = MainActivity.getVcam(Integer.parseInt(view.getTag().toString())).getVcamURL();
        Fragment newFragment = VcamPlayerFragment.newInstance(steamURL);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, newFragment, VcamPlayerFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onArchButtonClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment newFragment = VarchFragment.newInstance(mUserToken, v.getTag().toString());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(TAG);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    /**
     * REST Request for Vcam List
     */
    public void getCustomerVCamList() {

        pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCustomerVCamList");
        rp.setParam("customer", mUserToken);

        getCustomerVCamListAsyncTask task = new getCustomerVCamListAsyncTask();
        task.execute(rp);
    }
    public class getCustomerVCamListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getCustomerVCamList:" + s);
            MainActivity.setVcamList(VcamParser.parseFeed(s));
            updateDisplay();
        }
    }
}
