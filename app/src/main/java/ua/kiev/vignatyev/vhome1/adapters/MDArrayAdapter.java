package ua.kiev.vignatyev.vhome1.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ua.kiev.vignatyev.vhome1.HTTPManager;
import ua.kiev.vignatyev.vhome1.MainActivity;
import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectParserNew;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MDArrayAdapter extends ArrayAdapter<MotionDetectNew> {
    private Context context;
    private List<MotionDetectNew> mMotionDetectList;
    private LruCache< String, Bitmap > imageCache;
    private RequestQueue queue;
    private ImagePagerAdapter imagePagerAdapter;
    //private ViewPager viewPager;


    public MDArrayAdapter(Context context, int resource, List<MotionDetectNew> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mMotionDetectList = objects;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        Log.d("MyApp", "MEM -> " + cacheSize);
        imageCache = new LruCache<>(cacheSize);
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewPager viewPager;
        View view;
        Log.d("MyApp","MDArrayAdapter::getView: " + position + " " + convertView);

        if(convertView !=null){
            viewPager = (ViewPager) convertView.findViewById(R.id.view_pager);
            viewPager.setAdapter(null);

            int i = (int) convertView.getTag();
            mMotionDetectList.get(i).viewPager = null;
            view = convertView;
        } else {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_motion_detect, parent, false);
        }
        final MotionDetectNew motionDetect = mMotionDetectList.get(position);

        TextView tvNotificationDate = (TextView) view.findViewById(R.id.tvNotificationDate);
        TextView tvNotificationCamName = (TextView) view.findViewById(R.id.tvNotificationCamName);

        tvNotificationDate.setText(motionDetect.date);
        tvNotificationCamName.setText(motionDetect.cam_name);

        Button btFindInArchive = (Button) view.findViewById(R.id.btFindInArchive);
        btFindInArchive.setTag(motionDetect.iMotionDetect);
        btFindInArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MyApp", "Find In Archive Click " + view.getTag().toString());

            }
        });

        view.setTag(position);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        imagePagerAdapter = new ImagePagerAdapter(context, motionDetect.images, motionDetect.iMotionDetect, imageCache, queue);
        viewPager.setAdapter(imagePagerAdapter);

        motionDetect.viewPager = viewPager;
        return view;
    }

    @Override
    public void clear() {
        Log.d("MyApp", "MDArrayAdapter::clear");
        ViewPager viewPager;
        for(MotionDetectNew motionDetectNew : mMotionDetectList ) {
            if(motionDetectNew.viewPager != null) {
                //viewPager = (ViewPager) motionDetectNew.view;
                motionDetectNew.viewPager.setAdapter(null);
            }
        }
        super.clear();
    }
    /**
     * REST Request for getMotionDetectListByCustomer
     */
    private void getMotionDetectListByCustomer() {
        //pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMotionDetectList");
        rp.setParam("user_token", mUserToken);
        rp.setParam("start", Integer.toString(mMdLoadedItems));
        rp.setParam("length", Integer.toString(loadStep));
        getMotionDetectListAsyncTask task = new getMotionDetectListAsyncTask();
        task.execute(rp);
    }
    private class getMotionDetectListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            if(s == null)
                return;
            //Log.d("MyApp", "getMotionDetectListByCustomer replay" + ": " + s.length());
            JSONObject mdObject;
            try {
                mMdLoadedItems += loadStep;
                mdObject = new JSONObject(s);
                if(mdObject.has("md_list")) {
                    //JSONArray mdArray = mdObject.getJSONArray("md_list");
                    //mMotionDetectList = MotionDetectParserNew.parseFeed(mMotionDetectList, mdArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                //pd.hide();
                //updateDisplay();
            }
        }
    }
}
