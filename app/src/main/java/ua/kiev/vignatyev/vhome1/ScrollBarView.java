package ua.kiev.vignatyev.vhome1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ScrollBarView extends View {

    private static final String DEBUG_TAG = "MyApp";
    private static final float MAX_SCALE_FACTOR = 10f;
    private static final float MIN_SCALE_FACTOR = 0.0625f;
    private static final int DEFAULT_SCALE_SEC = (int) (1.5 * 60 * 60); // seconds 1.5
    private static final Integer MD_START = 0;
    private static final Integer MD_END = 1;

    private int mViewX, mViewY;

    private Paint rulerPaint, fontPaint, archivePaint, mdPaint;

    private ScrollBarView scrollBarView = this;

    private static final SimpleDateFormat format = new SimpleDateFormat("k");
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /* Motion Detect Array */
    private JSONArray mdArray;
    private Map< Date, Integer> mArchiveMap = null;
    private List<Date> mMDList = null;
    /**      */
    private Date mCurrentDate;
    /** scale length in seconds*/
    private int mScaleSec;

    private int mICustomerVcam = 143;

    private float mScaleFactor = 1.f;

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    private boolean mIsScrolling = false;

    public void setCurrentDate(Date date){
        mCurrentDate = date;
        invalidate();
    }

    public ScrollBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCurrentDate = new Date();
        mScaleSec = (int)(DEFAULT_SCALE_SEC * mScaleFactor); // sec

        rulerPaint = new Paint();
        rulerPaint.setStyle(Paint.Style.STROKE);
        rulerPaint.setStrokeWidth(0);
        rulerPaint.setAntiAlias(false);
        rulerPaint.setColor(Color.BLACK);

        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(20);
        fontPaint.setStyle(Paint.Style.STROKE);

        archivePaint = new Paint();
        archivePaint.setColor(Color.rgb(200, 200, 200));
        archivePaint.setStrokeWidth(0);

        mdPaint = new Paint();
        mdPaint.setStyle(Paint.Style.STROKE);
        mdPaint.setStrokeWidth(0);
        mdPaint.setAntiAlias(false);
        mdPaint.setColor(Color.YELLOW);


        mGestureDetector = new GestureDetectorCompat( context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public ScrollBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getArchiveList();
    }

    private int getXOffset(Date date){
        int offsetSec = (int)((date.getTime() - mCurrentDate.getTime())/1000) + (mScaleSec>>1);
        if(offsetSec < 0) {
            offsetSec = 0;
        } else if(offsetSec > mScaleSec) {
            offsetSec = mScaleSec;
        }
        return (mViewX * offsetSec) / mScaleSec;
    }

    private String date2String(Date currentDate, int diffSec){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.SECOND, diffSec);
        return(mMysqlDateFormat.format(calendar.getTime()));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("MyApp", "ScrollBarView::onSizeChanged mViewX= " + w + " mViewY= " + h);
        super.onSizeChanged(w, h, oldw, oldh);

        mViewX = w;
        mViewY = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mArchiveMap == null)
            return;
        //*** Draw video archive fragments
        int prevX = 0;
        int currX;

        for( Map.Entry<Date, Integer> md : mArchiveMap.entrySet()){
            currX = getXOffset(md.getKey());
            if(currX != 0 ){
                if( md.getValue() == MD_START ) {
                    prevX = currX;
                } else {
                    canvas.drawRect(prevX, 0, currX, mViewY, archivePaint);
                }
            }
        }
        //*** Draw motion detects
        for(Date mdDate : mMDList ){
            currX = getXOffset(mdDate);
            if(currX != 0 ){
                canvas.drawLine(currX,0,currX, mViewY, mdPaint);
            }
        }
        //*** Draw Scale
        int x, size;
        int hours, minutes, seconds;
        Date scaleStart = new Date(mCurrentDate.getTime() - (mScaleSec>>1) * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scaleStart);
        seconds = calendar.get(Calendar.SECOND);
        if(seconds != 0)
            calendar.add(Calendar.SECOND, 60 - seconds);
        minutes = calendar.get(Calendar.MINUTE);
        if(minutes != 0)
            calendar.add(Calendar.MINUTE, 60 - minutes);
        //**************
        canvas.drawLine(mViewX >> 1, 0, mViewX >> 1, mViewY, rulerPaint);
        canvas.drawText(mDateFormat.format(mCurrentDate), mViewX >> 1 + 5, 28, fontPaint);

        while ( (x = getXOffset(calendar.getTime())) < mViewX) {
            minutes = calendar.get(Calendar.MINUTE);
            hours = calendar.get(Calendar.HOUR);
            if (minutes == 30) {
                size = 10;
            } else if ((hours % 3) == 0) {
                canvas.drawText(format.format(calendar.getTime()), x - 12, mViewY - 28, fontPaint);
                size = 26;
            } else {
                size = 16;
            }
            canvas.drawLine(x, mViewY - size, x, mViewY, rulerPaint);
            calendar.add(Calendar.MINUTE, 30);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        this.mGestureDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);

        if( event.getAction() == MotionEvent.ACTION_UP){
            if(mIsScrolling) {
                mIsScrolling = false;
                getArchiveList();
            }
        }
        return true;
    }

    /**
     *
     */
    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mIsScrolling = true;

            mCurrentDate.setTime(mCurrentDate.getTime() + (long)distanceX * (mScaleSec<<2));

            scrollBarView.invalidate();

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     *
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(MAX_SCALE_FACTOR, mScaleFactor));

            mScaleSec = (int)(DEFAULT_SCALE_SEC / mScaleFactor);

            scrollBarView.invalidate();

            return super.onScale(detector);
        }
    }

    /**
     * REST Request for getArchiveList
     */
    private void getArchiveList() {
        //pd.show();
        int scaleHalf = mScaleSec >> 1;

        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam( "functionName", "getArchiveList");
        rp.setParam( "iCustomerVcam", Integer.toString(mICustomerVcam));
        rp.setParam( "startTime", date2String(mCurrentDate, - scaleHalf - 300) );
        rp.setParam( "endTime", date2String(mCurrentDate, scaleHalf + 300) );
        rp.setParam("scaleDivision", Integer.toString(0));
        getArchiveListAsyncTask task = new getArchiveListAsyncTask();
        task.execute(rp);
    }
    private class getArchiveListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String getArchiveListResponse) {
            JSONObject getArchiveListObj, obj;
            JSONArray archiveList;

            if (getArchiveListResponse == null)
                return;

            Log.d("MyApp", "getMotionDetectList replay: " + getArchiveListResponse);

            try {
                getArchiveListObj = new JSONObject(getArchiveListResponse);
                if(getArchiveListObj.has("archive_list")) {
                    archiveList = getArchiveListObj.getJSONArray("archive_list");
                    mArchiveMap = null;
                    mArchiveMap = new LinkedHashMap<Date, Integer>(archiveList.length() *2);
                    for (int i = 0; i < archiveList.length(); i++) {
                        obj = archiveList.getJSONObject(i);

                        if (obj.has("START_TIME")) {
                            mArchiveMap.put(mMysqlDateFormat.parse(obj.getString("START_TIME")), MD_START);
                        }
                        if (obj.has("END_TIME")) {
                            mArchiveMap.put(mMysqlDateFormat.parse(obj.getString("END_TIME")), MD_END);
                        }
                    }
                }
                getMDEventList();
             } catch (JSONException e) {
                e.printStackTrace();
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * REST Request for getMDEventList
     */
    private void getMDEventList() {
        //pd.show();
        int scaleHalf = mScaleSec >> 1;

        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam( "functionName", "getMDEventList");
        rp.setParam( "iCustomerVcam", Integer.toString(mICustomerVcam));
        rp.setParam( "startTime", date2String(mCurrentDate, - scaleHalf - 300) );
        rp.setParam( "endTime", date2String(mCurrentDate, scaleHalf + 300) );
        getMDEventListAsyncTask task = new getMDEventListAsyncTask();
        task.execute(rp);
    }
    private class getMDEventListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String getMDEventListAsyncTaskResponse) {
            JSONObject getMDEventListObj, obj;

            if (getMDEventListAsyncTaskResponse == null)
                return;

            Log.d("MyApp", "getMDEventListAsyncTask replay: " + getMDEventListAsyncTaskResponse);

            try {
                getMDEventListObj = new JSONObject(getMDEventListAsyncTaskResponse);
                if(getMDEventListObj.has("md_event_list")) {
                    JSONArray mdEventJSONArray = getMDEventListObj.getJSONArray("md_event_list");
                    mMDList = null;
                    mMDList = new ArrayList<Date>(mdEventJSONArray.length() * 2);
                    for (int i = 0; i < mdEventJSONArray.length(); i++) {
                        obj = mdEventJSONArray.getJSONObject(i);
                        if (obj.has("EVENT_TIME")) {
                            mMDList.add(mMysqlDateFormat.parse(obj.getString("EVENT_TIME")));
                        }
                    }
                }
                scrollBarView.invalidate();
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
