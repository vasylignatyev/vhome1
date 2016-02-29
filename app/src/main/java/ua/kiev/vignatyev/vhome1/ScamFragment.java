package ua.kiev.vignatyev.vhome1;

import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import ua.kiev.vignatyev.vhome1.adapters.VcamArrayAdapter;
import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.Vcam;
import ua.kiev.vignatyev.vhome1.parsers.VcamParser;

public class ScamFragment extends Fragment implements AbsListView.OnItemClickListener, VcamArrayAdapter.OnAdapterInteractionListener {
    /**
     * STATIC VAR
     */
    private static final String USER_TOKEN = "user_token";
    private static final String TAG = "ScamFragment";
    /**
     * VARS
     */
    private ProgressDialog pd;
    private String mUserToken;
    private AbsListView mListView;
    private String mStreamURL;

    /**
     *
     */
    public ScamFragment() {
    }

    /**
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
     * LIFE CICLE
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        pd = new ProgressDialog(context);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
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
        View view = inflater.inflate(R.layout.fragment_scam_list, container, false);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getSharedVCamList();
    }

    /**
     * SERVICE FUNCTION
     */
    public void updateDisplay() {
        VcamArrayAdapter vcamArrayAdapter = new VcamArrayAdapter(getActivity(), R.layout.item_vcam, MainActivity.getScamList());
        vcamArrayAdapter.setOnAdapterInteractionListener(this);
        if (null != vcamArrayAdapter) {
            mListView.setAdapter(vcamArrayAdapter);
        }
        pd.hide();
    }

    /**
     * IMPLEMENTED
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //set arguments
        Vcam vcam = MainActivity.getScam(Integer.parseInt(view.getTag().toString()));
        mStreamURL = vcam.getVcamURL();
        getHashString(vcam.getTOKEN());
    }

    //video Archive Button Click
    @Override
    public void onArchButtonClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment newFragment = VarchPlayerFragment.newInstance(mUserToken, v.getTag().toString());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(TAG);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    /**
     * REST Request for Shared Vcam List
     */
    public void getSharedVCamList() {

        pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
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
            MainActivity.setScamList(VcamParser.parseFeed(s));
            updateDisplay();
        }
    }

    /**
     * REST Request for Hash String
     */
    public void getHashString(String camToken) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "get_hash_string");
        rp.setParam("token", mUserToken);
        rp.setParam("cam_token", camToken);

        getHashStringAsyncTask task = new getHashStringAsyncTask();
        task.execute(rp);
    }

    public class getHashStringAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getHashString:" + s);

            JSONObject obj;
            String hash_string;
            try {
                obj = new JSONObject(s);
                if (obj.has("hash_string")) {
                    hash_string = obj.getString("hash_string");
                    mStreamURL += "?" + hash_string;
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                Fragment newFragment = VcamPlayerFragment.newInstance(mStreamURL);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.replace(R.id.container, newFragment, VcamPlayerFragment.TAG);
                transaction.addToBackStack(null);
                transaction.commit();
                fragmentManager.executePendingTransactions();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
