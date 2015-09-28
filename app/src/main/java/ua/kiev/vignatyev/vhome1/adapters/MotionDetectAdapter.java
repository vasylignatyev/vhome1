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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

import ua.kiev.vignatyev.vhome1.HTTPManager;
import ua.kiev.vignatyev.vhome1.MainActivity;
import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.MotionDetect;
import ua.kiev.vignatyev.vhome1.models.MotionDetectEvent;
import ua.kiev.vignatyev.vhome1.parsers.MotionDetectEventParser;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MotionDetectAdapter extends ArrayAdapter<MotionDetect> {
    private Context context;
    private List<MotionDetect> mMotionDetectList;
    private LruCache<String, Bitmap> imageCache;
    private RequestQueue queue;
    private ImageAdapter imageAdapter;
    //private ViewPager viewPager;


    public MotionDetectAdapter(Context context, int resource, List<MotionDetect> objects) {
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

        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_motion_detect, parent, false);

        final MotionDetect motionDetect = mMotionDetectList.get(position);

        TextView tvNotificationDate = (TextView) view.findViewById(R.id.tvNotificationDate);
        TextView tvNotificationCamName = (TextView) view.findViewById(R.id.tvNotificationCamName);

        tvNotificationDate.setText(motionDetect.date);
        tvNotificationCamName.setText(motionDetect.camName);

        view.setTag(motionDetect.iMotionDetect);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        imageAdapter = new ImageAdapter(context, motionDetect.images, position, imageCache, queue);

        final MotionDetectAdapter motionDetectAdapter = this;

        if(motionDetect.images == null) {
            MotionDetectAndViev motionDetectAndViev = new MotionDetectAndViev(motionDetect,viewPager, imageAdapter);
            genMotionDetectPopup(motionDetectAndViev);
        }
        viewPager.setAdapter(imageAdapter);
        return view;
    }
    private void setViewPagerAdapter(ViewPager v, MotionDetect motionDetect){
        //ImageAdapter adapter = new ImageAdapter(context, motionDetect.images, ,imageCache, queue);
        //v.setAdapter(adapter);
    }

    private class MotionDetectAndViev{
        public MotionDetect motionDetect;
        public ViewPager viewPager;
        ImageAdapter imageAdapter;

        public MotionDetectAndViev(MotionDetect motionDetect, ViewPager viewPager, ImageAdapter imageAdapter) {
            this.motionDetect = motionDetect;
            this.viewPager = viewPager;
            this.imageAdapter = imageAdapter;
        }
    }
    /**
     * REST Request for genMotionDetect Popup
     */
    private void genMotionDetectPopup(MotionDetectAndViev motionDetectAndViev) {
        genMotionDetectPopupAsyncTask task = new genMotionDetectPopupAsyncTask();
        task.execute(motionDetectAndViev);
    }

    /**
     * Async taskfor getNotification Table
     */
    private class genMotionDetectPopupAsyncTask extends AsyncTask<MotionDetectAndViev, Void, Void> {

        @Override
        protected Void doInBackground(MotionDetectAndViev... params) {
            MotionDetectAndViev motionDetectAndViev = params[0];

            MotionDetect motionDetect = motionDetectAndViev.motionDetect;
            ViewPager viewPager = motionDetectAndViev.viewPager;
            ImageAdapter imageAdapter = motionDetectAndViev.imageAdapter;

            RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/genMotionDetectPopup.php");
            rp.setMethod("GET");
            rp.setParam("token", MainActivity.getUserToken());
            rp.setParam("id", Integer.toString(motionDetect.iMotionDetect));

            String replay = HTTPManager.getData(rp);
            MotionDetectEvent motionDetectEvent = MotionDetectEventParser.parseFeed(replay);
            if( null != motionDetectEvent) {
                motionDetect.setEventParams(motionDetectEvent);
            }
            Log.d("MyApp", "Adapter notify");
            imageAdapter.notifyDataSetChanged();

            return null;
        }
    }
}
