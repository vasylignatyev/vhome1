package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import ua.kiev.vignatyev.vhome1.models.Vcam;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VcamPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VcamPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VcamPlayerFragment extends Fragment {

    public static final String TAG = "VcamPlayerFragment";

    private int mVcamPosition;
    private Vcam mVcam;
    private String mStreamURL;
    private int mOldOptions;


    VideoView video_player_view;
    DisplayMetrics dm;
    MediaController media_Controller;

    private MainActivity mMainActivity;

    Timer myTimer;

    private static final String VCAM_URL = "vcamUrl";

    public static VcamPlayerFragment newInstance(String vcamUrl) {

        VcamPlayerFragment fragment = new VcamPlayerFragment();
        Bundle args = new Bundle();
        args.putString(VCAM_URL, vcamUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public void getInit() {
        //set flag that inform player is started

        video_player_view = (VideoView) getActivity().findViewById(R.id.video_player_view);
        media_Controller = new MediaController(getActivity());
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        Log.d("MyApp", "height = " + height);
        int width = dm.widthPixels;
        Log.d("MyApp", "width = " + width);
        video_player_view.setMinimumWidth(width);
        video_player_view.setMinimumHeight(height);
        video_player_view.setMediaController(media_Controller);
        video_player_view.setVideoPath(mStreamURL);
        video_player_view.start();
    }

    public VcamPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        mMainActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStreamURL = getArguments().getString(VCAM_URL);
            mVcam = MainActivity.getVcam(mVcamPosition);

            //mStreamURL = "http://" + mVcam.URL + ":" + mVcam.HLS + "/myapp/"+ mVcam.TOKEN + "/index.m3u8";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_palyer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        Log.d("MyApp","onStart");
        super.onStart();

        runStream(mVcam.TOKEN);

        myTimer = new Timer(); // Создаем таймер
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                keepAliveStream();
            }
        }, 10000L, 10000L);
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d("MyApp", "onStop");
        myTimer.cancel();
    }

    @Override
    public void onDestroyView() {
        android.app.ActionBar actionBar = mMainActivity.getActionBar();
        mMainActivity.getWindow().getDecorView().setSystemUiVisibility(mOldOptions);
        actionBar.show();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void keepAliveStream() {
        Log.d("MyApp", "keepAliveStream position: " + mVcamPosition);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/functions/streamControl.php");
        rp.setMethod("GET");
        rp.setParam("token", mVcam.TOKEN);
        rp.setParam("appname", "myapp");
        rp.setParam("server_ip", "0.0.0.0");
        rp.setParam("sessname", "sessname");
        rp.setParam("action", "keepAlive");
        rp.setParam("hls", "true");

        KeepAliveStreamAsyncTask task = new KeepAliveStreamAsyncTask();
        task.execute(rp);
    }
    
    /**
     * Async task Start Vcam Streaming
     */
    public class KeepAliveStreamAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","KeepAliveStreamAsyncTask Replay: " + s);
        }
    }

    /**
     * REST Request for Vcam List
     */
    public void runStream(String token) {
        Log.d("MyApp", "runStream token: " + token);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/functions/streamControl.php");
        rp.setMethod("GET");
        rp.setParam("token", token);
        rp.setParam("appname", "myapp");
        rp.setParam("server_ip", "0.0.0.0");
        rp.setParam("sessname", "sessname");
        rp.setParam("action", "start");
        rp.setParam("hls", "true");

        RunStreamAsyncTask task = new RunStreamAsyncTask();
        task.execute(rp);
    }

    /**
     * Async task Start Vcam Streaming
     */
    public class RunStreamAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","runStreamAsyncTask Replay: " + s);
            showIfStreamExists();
        }
    }

    /**
     * REST if straem Exist
     */
    public void showIfStreamExists() {
        Log.d("MyApp", "showIfStreamExists url: " + mStreamURL);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/functions/showIfStreamExists.php");
        rp.setMethod("POST");
        rp.setParam("url", mStreamURL);

        ShowIfStreamExistsTask task = new ShowIfStreamExistsTask();
        task.execute(rp);
    }
    /**
     * Async task Start Vcam Streaming
     */
    public class ShowIfStreamExistsTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","ShowIfStreamExistsTask Replay: " + s);
            //if(s.equals("\"200\"")) {
                Log.d("MyApp", "Sream Ready");
                getInit();
            //}
        }
    }

}
