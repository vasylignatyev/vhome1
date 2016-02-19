package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.Credentials;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements  View.OnClickListener {
    public static final String PREFS_NAME = "VhomeSharedPreferences";
    public static final String TAG = "LoginFragment";

    private EditText etEmail;
    private EditText etPassword;
    private CheckBox cbSavePassword;

    private String mUserToken;
    private String mUserName;
    private String mUserPass;

    private MainActivity mMainActivity;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
    }

    /**
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
        try {
            mListener = (OnLoginFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }

    }

    /**
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getActivity());

        Credentials credentials = mListener.getCredentials();
        mUserName = credentials.getUserName();
        mUserPass = credentials.getUserPass();
        //cbSavePassword.setChecked(credentials.isSavePassword());

        populateViewForOrientation(inflater, frameLayout);



        return frameLayout;
    }
    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View view = inflater.inflate(R.layout.fragment_login, viewGroup);

        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        cbSavePassword = (CheckBox) view.findViewById(R.id.cbSavePassword);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        Button btnRegisterRecovery = (Button) view.findViewById(R.id.btnRegisterRecovery);

        etEmail.setText(mUserName);
        etPassword.setText(mUserPass);
        //cbSavePassword.setChecked(credentials.isSavePassword());

        btnLogin.setOnClickListener(this);
        btnRegisterRecovery.setOnClickListener(this);
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     */
    public interface OnLoginFragmentInteractionListener {
        void loggedIn(String user_token, String user_name, String user_pass);
        Credentials getCredentials();
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnLogin :
                //RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/createToken.php");
                RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/ajax.php");
                rp.setMethod("GET");
                mUserName = etEmail.getText().toString();
                mUserPass = etPassword.getText().toString();
                rp.setParam("functionName", "create_token");
                rp.setParam("user_email", mUserName);
                rp.setParam("user_pass", mUserPass);
                GetToken task = new GetToken();
                task.execute(rp);
                break;
            case  R.id.btnRegisterRecovery :
                Fragment newFragment = RegistrationFragment.newInstance(etEmail.getText().toString());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
                break;
        }

    }

    /**
     *
     */
    public class GetToken extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp",TAG + ": " + s);

            try {
                JSONObject obj = new JSONObject(s);
                Log.d("MyApp", obj.toString());
                if (obj.has("error")) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Wrong, password!!!", Toast.LENGTH_LONG);
                    toast.show();
                } else if (obj.has("token")) {
                    if(mListener != null){
                        String userToken = obj.getString("token");
                        mListener.loggedIn(userToken, mUserName, mUserPass);
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"SERVER CONNECTION ERROR!!!", Toast.LENGTH_LONG);
                toast.show();
            }
            //mNavigationDrawerFragment.getmDrawerListView().deferNotifyDataSetChanged();
        }
    }


}
