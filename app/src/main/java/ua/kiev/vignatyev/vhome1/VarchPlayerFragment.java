package ua.kiev.vignatyev.vhome1;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import ua.kiev.vignatyev.vhome1.models.Varch;


public class VarchPlayerFragment extends Fragment {
    private static final String TAG = "VarchPlayerFragment";

    private static final String ARG_VARCH_NAME = "varchName";
    private static final String ARG_USER_TOKEN = "userToken";
    private static final String ARG_VCAM_TOKEN = "vcamToken";

    private String mVarchName;
    private String mUserToken;
    private String mVcamToken;

    private MainActivity mMainActivity;

    private VideoView videoView;

    private String mVarchLinkName;

    private ProgressDialog pd;
    private int mOldOptions;

    public static VarchPlayerFragment newInstance(String varchName , String userToken,  String vcamToken) {
        VarchPlayerFragment fragment = new VarchPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VARCH_NAME, varchName);
        args.putString(ARG_USER_TOKEN, userToken);
        args.putString(ARG_VCAM_TOKEN, vcamToken);
        fragment.setArguments(args);
        return fragment;
    }

    public VarchPlayerFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pd = new ProgressDialog(activity);
        pd.setTitle("Загрузка видео");
        pd.setMessage("Ожидайте");
        pd.show();
        mMainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVarchName = getArguments().getString(ARG_VARCH_NAME);
            mUserToken = getArguments().getString(ARG_USER_TOKEN);
            mVcamToken = getArguments().getString(ARG_VCAM_TOKEN);
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
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_varch_player, container, false);
        //
        videoView = (VideoView) view.findViewById(R.id.varch_player_view);
        MediaController mediaController = new MediaController(getActivity());

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            public void onPrepared(MediaPlayer mp)
            {
                pd.dismiss();
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("myApp","video finished");
                getVarchNext();
                varchURL();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        mMainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        Log.d("MyApp", "height = " + height);
        int width = dm.widthPixels;
        Log.d("MyApp", "width = " + width);
        videoView.setMinimumWidth(width);
        videoView.setMinimumHeight(height);
        videoView.setMediaController(mediaController);
        //Убираем все ненужное
        android.app.ActionBar actionBar = mMainActivity.getActionBar();
        mOldOptions = mMainActivity.getWindow().getDecorView().getSystemUiVisibility();
        int newOptions = mOldOptions;
        newOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
        newOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        newOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        mMainActivity.getWindow().getDecorView().setSystemUiVisibility(newOptions);
        actionBar.hide();

        return view;
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        varchURL();
    }

    /**
     *
     */
    @Override
    public void onDestroyView() {
        Log.d("MyApp", TAG + ": onDestroyView" );
        android.app.ActionBar actionBar = mMainActivity.getActionBar();
        mMainActivity.getWindow().getDecorView().setSystemUiVisibility(mOldOptions);
        actionBar.show();
        super.onDestroyView();
    }

    /**
     *
     */
    public interface OnVarchPlayerInteractionListener {
        void getNextVarchPart(Varch varch);
    }

    /**
     * REST Request for Varch URL
     */
    public void varchURL() {
        Log.d("MyApp", "vArchURL name : " + mVarchName);
        pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "varchURL");
        rp.setParam("user_token", mUserToken);
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("varch_name", mVarchName);

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
            try {
                JSONObject linkName = new JSONObject(s);
                mVarchLinkName = linkName.getString("url");
                Log.d("MyApp", "GetVarchURLAsyncTask Replay: " + mVarchLinkName);
                videoView.setVideoPath(getString(R.string.SERVER_URL) + mVarchLinkName);
                //videoView.start();
            } catch (JSONException ex) {
                ex.printStackTrace();
             }
        }
    }

    /**
     * REST Request for Varch URL
     */
    public void getVarchNext() {
        Log.d("MyApp", "getVarchNext name : " + mVarchName);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVarchNext");
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("varch_name", mVarchName);

        GetVarchNextAsyncTask task = new GetVarchNextAsyncTask();
        task.execute(rp);
    }

    /**
     * Async taskfor Varch URL
     */
    public class GetVarchNextAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject varchName = new JSONObject(s);
                mVarchName = varchName.getString("varch_name");
                Log.d("MyApp", "GetVarchNextAsyncTask Replay: " + mVarchName);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

}
