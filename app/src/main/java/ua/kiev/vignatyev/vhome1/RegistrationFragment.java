package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.validator.TextValidator;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link RegistrationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "RegistrationFragment";
    private static final String ARG_EMAIL = "eMail";


    // TODO: Rename and change types of parameters
    private String mEMail;
    private Activity mActivity;
    private EditText mEtEmail;
    private EditText mEtPass1;
    private EditText mEtPass2;
    private Button btRegistration;

    Drawable mOriginalBackground;

    public static RegistrationFragment newInstance(String eMail) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, eMail);
        fragment.setArguments(args);
        return fragment;
    }

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEMail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mEtEmail = (EditText)view.findViewById(R.id.etEmail);
        mEtPass1 = (EditText)view.findViewById(R.id.etPass1);
        mEtPass2 = (EditText)view.findViewById(R.id.etPass2);
        btRegistration = (Button)view.findViewById(R.id.btRegistration);

        mEtEmail.addTextChangedListener(new TextValidator(mEtEmail) {
            @Override
            public void validate(TextView textView, String text) {
                Log.d("MyApp", text);
            }
        });
        mOriginalBackground = mEtEmail.getBackground();

        mEtEmail.setText(mEMail);

        mEtEmail.setOnFocusChangeListener(this);
        mEtPass1.setOnFocusChangeListener(this);
        mEtPass2.setOnFocusChangeListener(this);
        btRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomer();
            }
        });

        return view;
    }

    /**
     * 
     */
    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b) {
            switch (view.getId()) {
                case R.id.etEmail:
                    validateEmail(view);
                    break;
                case R.id.etPass1:
                    validatePassword(view);
                    break;
                case R.id.etPass2:
                    validatePassword2(view);
                    break;
            }
        } else {
            view.setBackgroundDrawable(mOriginalBackground);
        }

    }
    private void validateEmail(View v){
        TextView email = (TextView) v;
        Log.d("MyApp", "Validate Email: " + email.getText().toString());
        isEmailUniq(email.getText().toString());
    }
    private void validatePassword2(View view) {
        if (mEtPass1.getText().toString().equals(mEtPass2.getText().toString())) {
             //addCustomer();
         } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Пароли должны совпадать.", Toast.LENGTH_LONG);
            toast.show();
            mEtPass2.setError("Пароли должны совпадать");
         }
    }
    /**
     * REST Request for Vcam List
     */
    private void addCustomer(){
        RequestPackage rp = new RequestPackage(getString(R.string.SERVER_URL) + "/ajax/addCustomer.php");
        rp.setMethod("GET");
        //rp.setParam("functionName", "addCustomer");
        rp.setParam("userEmail", mEtEmail.getText().toString() );
        rp.setParam("userPassword", mEtPass1.getText().toString());

        addCustomerAsyncTask task = new addCustomerAsyncTask();
        task.execute(rp);

    }
    /**
     * Async taskfor Vcam List
     */
    private class addCustomerAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", s);
         }
    }
    /**
     * REST Request for Vcam List
     */
    private void validatePassword(View view){
        TextView v  = (TextView) view;
        RequestPackage rp = new RequestPackage(getString(R.string.SERVER_URL) + "/php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "validatePassword");
        rp.setParam("password", v.getText().toString() );

        validatePasswordAsyncTask task = new validatePasswordAsyncTask();
        task.execute(rp);

    }
    /**
     * Async taskfor Vcam List
     */
    private class validatePasswordAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.has("error")){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),obj.getString("error"), Toast.LENGTH_LONG);
                    toast.show();
                    mEtPass1.setError(obj.getString("error"));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    /**
     * REST Request for Vcam List
     */
    private void isEmailUniq(String userEmail) {
        //Log.d("MyApp", "getCustomerVCamList token: " + mUserToken);
        //pd.show();
        //************************
        RequestPackage rp = new RequestPackage(getString(R.string.SERVER_URL) + "/ajax/isEmailUniq.php");
        rp.setMethod("GET");
        rp.setParam("userEmail", userEmail);

        isEmailUniqAsyncTask task = new isEmailUniqAsyncTask();
        task.execute(rp);
    }
    /**
     * Async taskfor Vcam List
     */
    private class isEmailUniqAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            s = s.trim();
            //Log.d("MyApp", "Replay:'" + s +"'");
            if(s.equals("false")){
                mEtEmail.setError("Такой п/я уже используется!");
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Такой п/я уже используется!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


}