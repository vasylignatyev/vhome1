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
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;
import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;

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

}
