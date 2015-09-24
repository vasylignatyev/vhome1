package ua.kiev.vignatyev.vhome1;

import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.List;

import ua.kiev.vignatyev.vhome1.models.Credentials;
import ua.kiev.vignatyev.vhome1.models.Varch;
import ua.kiev.vignatyev.vhome1.models.Vcam;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
            VarchPlayerFragment.OnVarchPlayerInteractionListener, LoginFragment.OnLoginFragmentInteractionListener {

    /**
     * STATIC VARS
     */
    public static final int DIALOG_DATE = 1;
    public static final String SERVER_URL = "http://vhome.dev.oscon.com.ua/";

    private static final String PREFS_NAME = "VhomeSharedPreferences";
    private static List<Vcam> mVcamList = null;
    private static List<Vcam> mScamList = null;
    private static String mUserToken = null;
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    /**
     * VAR
     */
    private int myYear = 2011;
    private int myMonth = 02;
    private int myDay = 03;
    private int oldOptions;
    private boolean mLoggedIn = false;
    private SharedPreferences sp;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private String mUserName, mUserPass;
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
    public boolean isLoggedIn() {
        return mLoggedIn;
    }
    public String getUserName() {
        return mUserName;
    }
    public String getUserPass() {
        return mUserPass;
    }
    public static String getServerUrl() {
        return SERVER_URL;
    }
    public static String getUserToken() {
        return mUserToken;
    }
    public static int getScreenHeight() {
        return screenHeight;
    }
    public static int getScreenWidth() {
        return screenWidth;
    }

    /**
     *
     * @param newConfig
     */
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
            mUserName = sp.getString("userName", "");
            mUserPass = sp.getString("userPass", "");
        } else {
            mUserToken  = savedInstanceState.getString("userToken");
            mUserName   = savedInstanceState.getString("userName");
            mUserPass   = savedInstanceState.getString("userPass");
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Log.d("MyApp", "Screen: " + screenWidth + " X " + screenHeight);
        //GSM INIT
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(getBaseContext(),0,new Intent(),0));
        registrationIntent.putExtra("sender", "80369243567");
        startService(registrationIntent);
    }

    @Override
    protected void onDestroy() {
        Intent unregistrationIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        unregistrationIntent.putExtra("app",PendingIntent.getBroadcast(getBaseContext(),0,new Intent(),0));
        startService(unregistrationIntent);

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
        Fragment newFragment;
        String fragmentId = null;

        if(mLoggedIn == false) {
            newFragment = (Fragment) new LoginFragment();
        } else {
            switch (position) {
                case 0:
                    newFragment = (Fragment) VcamFragment.newInstance(mUserToken);
                    break;
                case 1:
                    newFragment = (Fragment) ScamFragment.newInstance(mUserToken);
                    break;
                case 2:
                    newFragment = (Fragment) MotionDetectFragment.newInstance(mUserToken);
                    break;
                case 3:
                    newFragment = (Fragment) SharedMotionDetectFragment.newInstance(mUserToken);
                    break;
                default:
                    newFragment = (Fragment) new LoginFragment();
                    break;
            }
        }
        fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
    }

    public void onSectionAttached(int number) {
    }

    public void restoreActionBar() {
        //ActionBar actionBar = getSupportActionBar();
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager =  getSupportFragmentManager();

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        //Log.d("MyApp", "backStackEntryCount: " + new Integer(backStackEntryCount).toString());
        Log.d("MyApp", "backStackEntryCount: " + backStackEntryCount);

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

            showDialog();
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
    public void loggedIn(String user_token, String user_name, String user_pass, boolean savePassword) {
        mUserToken = user_token;
        mLoggedIn = true;
        mUserName = user_name;
        mUserPass = user_pass;

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userName", mUserName);
        if(savePassword) {
            editor.putString("userPass", mUserPass);
        } else {
            editor.putString("userPass", "");
        }
        editor.putBoolean("savePassword", savePassword);
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
        newFragment = (Fragment) VcamFragment.newInstance(mUserToken);
        fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
    }

    @Override
    public Credentials getCredentials() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userName = sp.getString("userName", "vignatyev@list.ru");
        String userPass = sp.getString("userPass", "123456");
        Boolean savePassword = sp.getBoolean("savePassword", true);

        return new Credentials(userName, userPass, savePassword);
    }
}
