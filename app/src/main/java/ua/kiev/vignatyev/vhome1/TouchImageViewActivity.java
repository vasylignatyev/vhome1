package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class TouchImageViewActivity extends Activity {

    final static String I_MOTION_DETECT = "I_MOTION_DETECT";
    private int mIMotionDetect;
    private List<String> mImagesUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_image_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mIMotionDetect = extras.getInt(I_MOTION_DETECT);
            getMD_URL_List();
        }
        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);
        mViewPager.setAdapter(new TouchImageAdapter());
    }

    //**************************************************************
    static class TouchImageAdapter extends PagerAdapter {

        private static int[] images;

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            img.setImageResource(images[position]);
            container.addView( img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**
     * REST Request for Vcam List
     */
    public void getMD_URL_List() {

        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMD_URL_List");
        rp.setParam("i_motion_detect", Integer.toString(mIMotionDetect));

        getMD_URL_ListtAsyncTask task = new getMD_URL_ListtAsyncTask();
        task.execute(rp);
    }
    /**
     * Async taskfor Vcam List
     */
    public class getMD_URL_ListtAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getMD_URL_List:" + s);
        }
    }

}
