package ua.kiev.vignatyev.vhome1.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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

import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MotionDetectAdapterNew extends ArrayAdapter<MotionDetectNew> {
    private Context context;
    private List<MotionDetectNew> mMotionDetectList;
    private LruCache< String, Bitmap > imageCache;
    private RequestQueue queue;
    private ImageAdapter imageAdapter;
    //private ViewPager viewPager;


    public MotionDetectAdapterNew(Context context, int resource, List<MotionDetectNew> objects) {
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

        final MotionDetectNew motionDetect = mMotionDetectList.get(position);

        TextView tvNotificationDate = (TextView) view.findViewById(R.id.tvNotificationDate);
        TextView tvNotificationCamName = (TextView) view.findViewById(R.id.tvNotificationCamName);

        tvNotificationDate.setText(motionDetect.date);
        tvNotificationCamName.setText(motionDetect.cam_name);

        view.setTag(motionDetect.iMotionDetect);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        imageAdapter = new ImageAdapter(context, motionDetect.images, motionDetect.iMotionDetect, imageCache, queue);


        viewPager.setAdapter(imageAdapter);
        return view;
    }

    @Override
    public void clear() {
        Log.d("MyApp", "MotionDetectAdapterNew::clear");
        super.clear();
    }
}
