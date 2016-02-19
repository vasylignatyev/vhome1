package ua.kiev.vignatyev.vhome1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.CountDownTimer;
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

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;


public class ScrollBarView extends View {

    private static final float MAX_SCALE_FACTOR = 10f;
    private static final float MIN_SCALE_FACTOR = 0.0625f;
    private static final int DEFAULT_SCALE_SEC = (int) (1.5 * 60 * 60 * 1000); // milliseconds 1.5 hour
    private static final long OBSERVATION_PERIOD_DURATION = 2*24*60*60*1000; //milliseconds 2 days
    private static final Integer MD_START = 0;
    private static final Integer MD_END = 1;
    private static final SimpleDateFormat format = new SimpleDateFormat("k");
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int mViewX, mViewY;

    private Paint cursorPaint, fontPaint, archivePaint, mdPaint, backgroundPaint;

    private ScrollBarView scrollBarView = this;

    /* Motion Detect Array */
    private Map<Date,Integer> mArchiveMap = new LinkedHashMap<Date, Integer>();
    private List<Date> mMDList = new ArrayList<Date>();
    /** scale length in milliseconds*/
    private long mScaleSec;

    private int mICustomerVcam = 143;

    private float mScaleFactor = 1.f;

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    private boolean mIsScrolling = false;

    private ScrollBarViewInterface mListener = null;

    /**      */
    private Date mCurrentDate = new Date();
    private Date mObservationEnd  = mCurrentDate;
    private Date mObservationStart = new Date(mObservationEnd.getTime() - OBSERVATION_PERIOD_DURATION);

    CountDownTimer mCountDownTimer;

    /**
     * CONSTRUCTORS
     */
    public ScrollBarView(Context context) {
        super(context);
        init(context);
    }
    public ScrollBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context);
    }
    public ScrollBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init (Context context) {
        mCurrentDate = new Date();
        /** period of observation initialization */

        mScaleSec = (long)(DEFAULT_SCALE_SEC * mScaleFactor); // milliseconds

        cursorPaint = new Paint();
        cursorPaint.setStyle(Paint.Style.STROKE);
        cursorPaint.setStrokeWidth(0);
        cursorPaint.setAntiAlias(false);
        cursorPaint.setColor(Color.BLACK);

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

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStrokeWidth(0);


        mGestureDetector = new GestureDetectorCompat( context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        mCountDownTimer = new CountDownTimer( 5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                scrollBarView.slideDown();
            }
        };
    }
    /**
     * LIFE CYCLE
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //getArchiveList();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getArchiveList();
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

        canvas.drawRect(0, 0, mViewX, mViewY, backgroundPaint);

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
        if(mMDList != null ) {
            for (Date mdDate : mMDList) {
                currX = getXOffset(mdDate);
                if (currX != 0) {
                    canvas.drawLine(currX, 0, currX, mViewY, mdPaint);
                }
            }
        }
        //*** Draw Scale
        int x, size;
        int hours, minutes, seconds;
        Date scaleStart = new Date(mCurrentDate.getTime() - (mScaleSec>>1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scaleStart);
        seconds = calendar.get(Calendar.SECOND);
        if(seconds != 0)
            calendar.add(Calendar.SECOND, 60 - seconds);
        minutes = calendar.get(Calendar.MINUTE);
        if(minutes != 0)
            calendar.add(Calendar.MINUTE, 60 - minutes);
        //**************
        canvas.drawLine(mViewX >> 1, 0, mViewX >> 1, mViewY, cursorPaint);
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
            canvas.drawLine(x, mViewY - size, x, mViewY, cursorPaint);
            calendar.add(Calendar.MINUTE, 30);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){

        this.mGestureDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(mListener != null) {
                mListener.onChange();
            }
        }

        if( event.getAction() == MotionEvent.ACTION_UP){
            if(mIsScrolling) {
                mIsScrolling = false;
                mCountDownTimer.cancel();
                mCountDownTimer.start();
                if(mListener != null) {
                    mListener.onScroll(mCurrentDate);
                }
                //getArchiveList();
            }
        }
        return true;
    }

    /**
     * COMMUNICATIONS
     */
    public void setScrollListener(Object v){
        try {
            mListener = (ScrollBarViewInterface) v;
        } catch (ClassCastException e) {
            throw new ClassCastException(v.toString()
                    + " must implement ScrollBarViewInterface");
        }
    }

    /**
     * FUNCTIONS
     */
    public void setCurrentDate(Date date){
        mCurrentDate = date;
        invalidate();
    }
    public void slideDown() {
        animate().translationY(mViewY);
    }
    public void slideUp() {
        mCountDownTimer.cancel();
        mCountDownTimer.start();
        animate().translationY(0);
        bringToFront();
    }
    private int getXOffset(Date date){
        long offsetSec = date.getTime() - mCurrentDate.getTime() + mScaleSec>>1;
        //Log.d("MyApp", "offsetSec:" + offsetSec + " mScaleSec:" + mScaleSec);
        if(offsetSec < 0) {
            return 0;
        } else {
            if (offsetSec > mScaleSec) {
                return mViewX;
            }
        }
        return (int)( (mViewX * offsetSec) / mScaleSec );
    }

    /**
     *
     */
    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mIsScrolling = true;

            mCurrentDate.setTime(mCurrentDate.getTime() + (long) distanceX * (mScaleSec >> 8));
            /*
            if(mListener != null) {
                mListener.onScroll(mCurrentDate);
            }
            */
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
            mScaleFactor = mScaleFactor / detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(MAX_SCALE_FACTOR, mScaleFactor));

            mScaleSec = (int)(DEFAULT_SCALE_SEC * mScaleFactor);

            scrollBarView.invalidate();

            return super.onScale(detector);
        }
    }

    interface ScrollBarViewInterface {
        void onScroll(Date date);
        void onChange();
    }
    /**
     * REST Request for getArchiveList
     */
    private void getArchiveList() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam( "functionName", "getArchiveList");
        rp.setParam( "iCustomerVcam", Integer.toString(mICustomerVcam));
        rp.setParam( "startTime", mMysqlDateFormat.format(mObservationStart) );
        rp.setParam( "endTime", mMysqlDateFormat.format(mObservationEnd) );
        rp.setParam("scaleDivision", Integer.toString(0));
        getArchiveListAsyncTask task = new getArchiveListAsyncTask();
        task.execute(rp);
    }
    private class getArchiveListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
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
             } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * REST Request for getMDEventList
     */
    private void getMDEventList() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam( "functionName", "getMDEventList");
        rp.setParam( "iCustomerVcam", Integer.toString(mICustomerVcam));
        rp.setParam( "startTime", mMysqlDateFormat.format(mObservationStart) );
        rp.setParam( "endTime", mMysqlDateFormat.format(mObservationEnd) );
        getMDEventListAsyncTask task = new getMDEventListAsyncTask();
        task.execute(rp);
    }
    private class getMDEventListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
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
                    for (int i = 0; i < mdEventJSONArray.length(); i++) {
                        obj = mdEventJSONArray.getJSONObject(i);
                        if (obj.has("EVENT_TIME")) {
                            mMDList.add(mMysqlDateFormat.parse(obj.getString("EVENT_TIME")));
                        }
                    }
                }
                //scrollBarView.invalidate();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
