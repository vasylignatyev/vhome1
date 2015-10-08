package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class TouchImageViewActivity extends Activity {

    public final static String I_MOTION_DETECT = "I_MOTION_DETECT";
    private int mIMotionDetect;
    private static ArrayList<String> mImagesUrlList = new ArrayList<>();
    private ExtendedViewPager mViewPager;

    private static RequestQueue queue;

    private TouchImageAdapter touchImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_image_view);

        Log.d("MyApp", "TouchImageViewActivity::onCreate");

        queue = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mIMotionDetect = extras.getInt(I_MOTION_DETECT, 0);
            if(0 != mIMotionDetect) {
                Log.d("MyApp", "TouchImageViewActivity I_MOTION_DETECT: " + Integer.toString(mIMotionDetect));
                getMD_URL_List();
            }
        }
        mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MyApp", "TouchImageViewActivity::onDestroy");
       // touchImageAdapter.n
        mViewPager.setAdapter(touchImageAdapter);
    }

    //**************************************************************
    static class TouchImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImagesUrlList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final String uri = mImagesUrlList.get(position);
            final TouchImageView img = new TouchImageView(container.getContext());
            container.addView( img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            ImageRequest imageRequest = new ImageRequest( MainActivity.SERVER_URL + uri,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            Log.d("MyApp", "TouchImageAdapter load: " + uri);
                            //imageView.setImageBitmap(bitmap);
                            //imageCache.put(uri, bitmap);
                            img.setImageBitmap(bitmap);
                            //if (bitmap != null && !bitmap.isRecycled())
                                //bitmap.recycle();

                        }
                    }, 0,0,
                    Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d("MyApp",volleyError.getMessage());
                        }
                    }
            );
            queue.add(imageRequest);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView((View) object);
            container.removeView((View) object);
            Log.d("MyApp", "TouchImageAdapter::destroyItem: " + Integer.toString(position));
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
            try {
                JSONArray imageArray = new JSONArray(s);
                mImagesUrlList.clear();
                for(int i = 0 ; i < imageArray.length(); i++ ) {
                    String url = imageArray.getString(i);
                    mImagesUrlList.add(url);
                }
                if(touchImageAdapter == null) {
                    touchImageAdapter = new TouchImageAdapter();
                    mViewPager.setAdapter(touchImageAdapter);
                } else {
                    touchImageAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
