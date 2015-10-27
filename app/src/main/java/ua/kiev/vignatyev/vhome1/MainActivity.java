package ua.kiev.vignatyev.vhome1;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ua.kiev.vignatyev.vhome1.gcm.QuickstartPreferences;
import ua.kiev.vignatyev.vhome1.gcm.RegistrationIntentService;
import ua.kiev.vignatyev.vhome1.models.Credentials;
import ua.kiev.vignatyev.vhome1.models.Varch;
import ua.kiev.vignatyev.vhome1.models.Vcam;



public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
            VarchPlayerFragment.OnVarchPlayerInteractionListener, LoginFragment.OnLoginFragmentInteractionListener {

    /**
     * STATIC VARS
     */
    public static final String SERVER_URL = "http://vhome.dev.oscon.com.ua/";
    public static final String PREFS_NAME = "VhomeSharedPreferences";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static List<Vcam> mVcamList = null;
    private static List<Vcam> mScamList = null;
    private static String mUserToken = null;
    public static Context context;

    /**
     * VAR
     */
    private boolean mLoggedIn = false;
    private SharedPreferences sp;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private String mUserName, mUserPass;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * GETTERS AND SETTERS
     */
    public static Vcam getVcam(int position) {
        return mVcamList.get(position);
    }
    public static Vcam getScam(int position) {
        return mScamList.get(position);
    }
    public static void setVcamList(List<Vcam> vcamList) {
        MainActivity.mVcamList = vcamList;
    }
    public static void setScamList(List<Vcam> scamList) {
        MainActivity.mScamList = scamList;
    }
    public static List<Vcam> getVcamList(){return mVcamList;}
    public static List<Vcam> getScamList(){return mScamList;}
    public static Boolean isVcamListEmpty(){
        return null == mVcamList;
    }
    public static Boolean isScamListEmpty() {
        return null == mScamList;
    }
    public static String getUserToken() {
        return mUserToken;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(savedInstanceState == null) {
            mUserName = sp.getString("userName", null);
            mUserPass = sp.getString("userPass", null);
        } else {
            mUserToken  = savedInstanceState.getString("userToken");
            mUserName   = savedInstanceState.getString("userName");
            mUserPass   = savedInstanceState.getString("userPass", null);
        }
        confirmAuthentication();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d("MyApp", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userToken", mUserToken);
        outState.putString("userName", mUserName);
        outState.putString("userPass", mUserPass);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Log.d("MyApp", "onNavigationDrawerItemSelected position: " + new Integer(position).toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Claer Back Stack
        int count = fragmentManager.getBackStackEntryCount();
        for(int i = 0; i < count; ++i) {
            fragmentManager.popBackStackImmediate();
        }
        Fragment newFragment = null;

        if(mLoggedIn == false) {
            newFragment = new LoginFragment();
        } else {
            switch (position) {
                case 0:
                    newFragment = VcamFragment.newInstance(mUserToken);
                    break;
                case 1:
                    newFragment = ScamFragment.newInstance(mUserToken);
                    break;
                case 2:
                    newFragment = MDFragment.newInstance(mUserToken);
                    break;
                case 3:
                    newFragment = SharedMotionDetectFragment.newInstance(mUserToken);
                    break;
                case 4:
                    logout();
                    break;
                default:
                    newFragment = new LoginFragment();
                    break;
            }
        }
        if(null != newFragment)
            fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
    }

    public void logout() {
        mVcamList = null;
        mScamList = null;
        sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("userPass");
        editor.apply();
        finish();
    }


    public void restoreActionBar() {
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ((null != mNavigationDrawerFragment) && (!mNavigationDrawerFragment.isDrawerOpen())) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return (id == R.id.action_settings) ? true : super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager =  getSupportFragmentManager();

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        if( backStackEntryCount == 0 ) {
            /*
            AlertDialog.Builder alert = new AlertDialog.Builder(this.getApplicationContext());
            alert.setTitle(getString(R.string.exitQuestion));
            alert.setMessage(R.string.exitQuestion);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alert.show();
            */

            //showDialog();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
    void showDialog() {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(
                R.string.exitQuestion);
        newFragment.show(getFragmentManager(), "dialog");
    }
    public void doPositiveClick() {
        // Do stuff here.
        Log.i("MyApp", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("MyApp", "Negative click!");
    }
        @Override
    public void getNextVarchPart(Varch varch) {

    }
    @Override
    public void loggedIn(String user_token, String user_name, String user_pass) {
        mUserToken = user_token;
        mLoggedIn = true;
        mUserName = user_name;
        mUserPass = user_pass;
        /* */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                    Log.d("MyApp", "Token retrieved and sent to server! You can now use gcmsender to send downstream messages to this app.");
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                    Log.d("MyApp","An error occurred while either fetching the InstanceID token, sending the fetched token to the server or subscribing to the PubSub topic. Please try running the sample again.");
                }
            }
        };
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        /* */

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userName", mUserName);
        editor.putString("userPass", mUserPass);
        editor.apply();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        if (null != mNavigationDrawerFragment) {
            mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment;
        newFragment = VcamFragment.newInstance(mUserToken);
        fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
    }

    @Override
    public Credentials getCredentials() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userName = sp.getString("userName", null);
        String userPass = sp.getString("userPass", null);
        Boolean savePassword = sp.getBoolean("savePassword", true);

        Log.d("MyApp", "userName: " + userName + ", userPass: " + userPass);
        return new Credentials(userName, userPass, savePassword);
    }

    /**
     *
     */
    private void confirmAuthentication() {

        RequestPackage rp = new RequestPackage(getString(R.string.SERVER_URL) + "/php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "confirmAuthentication");
        rp.setParam("user_email", mUserName);
        rp.setParam("user_pass", mUserPass);
        confirmAuthenticationAsyncTask task = new confirmAuthenticationAsyncTask();
        task.execute(rp);
    }

    /**
     *
     */
    public class  confirmAuthenticationAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","confirmAuthenticationAsync replay" + ": " + s);

            try {
                JSONObject obj = new JSONObject(s);
                Log.d("MyApp", obj.toString());
                if (obj.has("error")) {
                    Toast toast = Toast.makeText(MainActivity.this, "Wrong, password!!!", Toast.LENGTH_LONG);
                    toast.show();
                } else if (obj.has("token")) {
                    //if(mListener != null){
                       String userToken = obj.getString("token");
                       loggedIn(userToken, mUserName, mUserPass);
                    //}
                }
            } catch(JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(MainActivity.this,"SERVER CONNECTION ERROR!!!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
