package ua.kiev.vignatyev.vhome1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.Segment;


public class ScrollBarView extends View {

    private static final float MAX_SCALE_FACTOR = 10f;
    private static final float MIN_SCALE_FACTOR = 0.0625f;
    private static final int DEFAULT_SCALE_SEC = (int) (1.5 * 60 * 60 * 1000); // milliseconds 1.5 hour
    private static final long OBSERVATION_PERIOD_DURATION = 2*24*60*60*1000; //milliseconds 2 days
    private static final SimpleDateFormat format = new SimpleDateFormat("k");
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int mViewX, mViewY;

    private Paint cursorPaint, fontPaint, archivePaint, mdPaint, backgroundPaint, scalePaint;
    private static final int cursorSize = 16;

    private ScrollBarView scrollBarView = this;

    /* Motion Detect Array */
    private List<Segment> mArchiveMap = new LinkedList<Segment>();
    private List<Segment> mMDList = new LinkedList<Segment>();

    /** scale length in milliseconds*/
    private long mScaleMillisec;

    private float mScaleFactor = 1.f;

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    private boolean mIsScrolling = false;

    private ScrollBarViewInterface mListener = null;

    /**      */
    private final Date mCurrentDate = new Date();
    private final Date mObservationEnd  = new Date(mCurrentDate.getTime());
    private final Date mObservationStart = new Date(mObservationEnd.getTime() - OBSERVATION_PERIOD_DURATION);

    /**
     * GETTERS AND SETTERS
     */
    public Date getCurrentDate() {
        return mCurrentDate;
    }
    public void setCurrentDate(Date date){
        setCurrentDate(date.getTime());
    }
    public void setCurrentDate(long currentDate) {
        mCurrentDate.setTime(currentDate);
        invalidate();
    }

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
        /** period of observation initialization */

        mScaleMillisec = (long)(DEFAULT_SCALE_SEC * mScaleFactor); // milliseconds

        cursorPaint = new Paint();
        cursorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //cursorPaint.setStrokeWidth(0);
        cursorPaint.setAntiAlias(true);
        cursorPaint.setColor(Color.RED);

        scalePaint = new Paint();
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setStrokeWidth(0);
        scalePaint.setAntiAlias(false);
        scalePaint.setColor(Color.BLACK);

        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(20);
        fontPaint.setStyle(Paint.Style.STROKE);
        fontPaint.setTextAlign(Paint.Align.CENTER);
        //fontPaint.Align();

        archivePaint = new Paint();
        archivePaint.setColor(Color.rgb(200, 200, 200));
        archivePaint.setStrokeWidth(0);

        mdPaint = new Paint();
        mdPaint.setStrokeWidth(0);
        mdPaint.setColor(Color.YELLOW);


        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(128, 128, 128));
        backgroundPaint.setStrokeWidth(0);


        mGestureDetector = new GestureDetectorCompat( context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        new CountDownTimer( 5000, 1000) {
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

        //*** Draw video archive fragments
        int prevX = 0;
        int currX;

        int halfScale = mViewX >> 1;
        int baseLine  = mViewY >> 1;

        canvas.drawRect(0, 0, mViewX, mViewY, backgroundPaint);
        //*** Draw archive
        if(mArchiveMap != null ) {
            int x1, x2;
            for (Segment segment : mArchiveMap) {
                x1 = getXOffset(segment.eventStart);
                x2 = getXOffset(segment.eventStop);
                if((x1  <= 0) & (x2 <= 0) | ((x1  >= mViewX)&(x2 >= mViewX))) {
                    continue;
                }
                if(x1 == x2) {
                    x2 = x2 + 1;
                }
                canvas.drawRect(x1, 0, x2, mViewY, archivePaint);
            }
        }
        //*** Draw motion detects
        if( (mListener != null) & (mListener.showMotionDetect())) {
            if (mMDList != null) {
                int x1, x2;
                for (Segment segment : mMDList) {
                    x1 = getXOffset(segment.eventStart);
                    if ((x1 <= 0)) {
                        continue;
                    }
                    if (segment.eventStart.getTime() == segment.eventStop.getTime()) {
                        canvas.drawLine(x1, 0, x1, mViewY, mdPaint);
                        continue;
                    }
                    x2 = getXOffset(segment.eventStop);
                    if (x2 >= mViewX) {
                        continue;
                    }
                    if (x1 == x2) {
                        x2 = x2 + 1;
                    }
                    canvas.drawRect(x1, 0, x2, mViewY, mdPaint);
                }
            }
        }
        //************** CURSOR

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(halfScale, cursorSize);
        path.lineTo(halfScale - cursorSize, 0);
        path.lineTo(halfScale + cursorSize, 0);
        path.lineTo(halfScale, cursorSize);

        path.moveTo(halfScale, mViewY - cursorSize);
        path.lineTo(halfScale - cursorSize, mViewY);
        path.lineTo(halfScale + cursorSize, mViewY);
        path.lineTo(halfScale, mViewY - cursorSize);

        path.close();
        canvas.drawPath(path, cursorPaint);


        //******************** Date
        canvas.drawText(mDateFormat.format(mCurrentDate), (mViewX >> 1) + 5, 28, fontPaint);

        //*** Draw Scale
        int x, size;
        int hours, minutes, seconds;

        Date scaleStart = new Date(mCurrentDate.getTime() - (mScaleMillisec >> 1));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scaleStart);
        seconds = calendar.get(Calendar.SECOND);
        if(seconds != 0)
            calendar.add(Calendar.SECOND, 60 - seconds);
        minutes = calendar.get(Calendar.MINUTE);
        if(minutes != 0)
            calendar.add(Calendar.MINUTE, 15 - minutes % 15);

        //***************** Scale
        while ( (x = getXOffset(calendar.getTime())) < mViewX) {
            minutes = calendar.get(Calendar.MINUTE);
            hours = calendar.get(Calendar.HOUR);
            size = 10;
            if (minutes == 30) {
                size = 16;
            }
            if (minutes == 0) {
                if ((hours % 3) == 0) {
                    canvas.drawText(format.format(calendar.getTime()), x, baseLine + 10, fontPaint);
                    size = 0;
                } else {
                    canvas.drawText(format.format(calendar.getTime()), x, baseLine + 10, fontPaint);
                    size = 0;
                }
            }
            if(size != 0)
                canvas.drawLine(x, baseLine - size, x, baseLine + size, scalePaint);
            calendar.add(Calendar.MINUTE, 15);
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
                onScrollFinishedEvent();
            }
        }
        return true;
    }

    /**
     * Scroll is finished
     */
    private void onScrollFinishedEvent() {
        Date stopPosition = null;

        if(mListener.showMotionDetect()) {
            stopPosition = getNextMD();
        }
        mListener.startPlay(stopPosition);
    }

    public Date getNextMD(){
        Log.d("MyApp", "--------------> getNextMD");
        for( Segment ev : mMDList ) {
            if (ev.eventStart.getTime() > mCurrentDate.getTime()) {
                setCurrentDate(ev.eventStart);
                Log.d("MyApp", "eventStart->" + ev.eventStart + "  eventStart->" + ev.eventStop);
                return ev.eventStop;
            }
        }
        return null;
    }

    /**
     * COMMUNICATIONS
     */
    public void setScrollListener(Object v){
        try {
            mListener = (ScrollBarViewInterface) v;
            getArchiveList();
        } catch (ClassCastException e) {
            throw new ClassCastException(v.toString()
                    + " must implement ScrollBarViewInterface");
        }
    }

    public void slideDown() {
        //animate().translationY(mViewY);
    }
    public void slideUp() {
        //animate().translationY(0);
        bringToFront();
    }
    private int getXOffset(Date date){
        long offsetSec = date.getTime() - mCurrentDate.getTime() + (mScaleMillisec >> 1);
        if(offsetSec <= 0) {
            return 0;
        }
        if (offsetSec >= mScaleMillisec) {
            return mViewX;
        }
        return (int)( (mViewX * offsetSec) / mScaleMillisec);
    }

    /**
     *
     */
    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mIsScrolling = true;

            setCurrentDate(mCurrentDate.getTime() + (long) distanceX * (mScaleMillisec >> 8));

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

            mScaleMillisec = (int)(DEFAULT_SCALE_SEC * mScaleFactor);

            scrollBarView.invalidate();

            return super.onScale(detector);
        }
    }

    interface ScrollBarViewInterface {
        void startPlay(Date stop);
        void onChange();
        Boolean showMotionDetect();
        String getVcamToken();
    }
    /**
     * REST Request for getArchiveList
     */
    private void getArchiveList() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam( "functionName", "getArchiveList");
        //rp.setParam( "iCustomerVcam", Integer.toString(VarchPlayerFragment.getICustomerVcam()));
        rp.setParam( "vcam_token", mListener.getVcamToken());
        rp.setParam( "startTime", mMysqlDateFormat.format(mObservationStart) );
        rp.setParam( "endTime", mMysqlDateFormat.format(mObservationEnd) );
        rp.setParam( "scaleDivision", Integer.toString(0));
        getArchiveListAsyncTask task = new getArchiveListAsyncTask();
        task.execute(rp);
    }
    private class getArchiveListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            JSONArray jsonArray, mdJson;

            if (s == null)
                return;

            Log.d("MyApp", "getMotionDetectList replay: " + s);

            try {
                jsonArray = new JSONArray(s);
                Log.d("MyApp", "Archive amount = " + jsonArray.length() );
                for( int i = 0 ; i < jsonArray.length() ; i++) {
                    mdJson = jsonArray.getJSONArray(i);
                    Segment segment = new Segment();
                    segment.eventStart = mMysqlDateFormat.parse(mdJson.getString(0));
                    segment.eventStop  = mMysqlDateFormat.parse(mdJson.getString(1));
                    mArchiveMap.add(segment);
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
            getMDEventList();
        }
    }
    /**
     * REST Request for getMDEventList
     */
    private void getMDEventList() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getMDEventList");
        //rp.setParam( "iCustomerVcam", Integer.toString(VarchPlayerFragment.getICustomerVcam()));
        rp.setParam( "vcam_token", mListener.getVcamToken());
        rp.setParam( "startTime", mMysqlDateFormat.format(mObservationStart) );
        rp.setParam( "endTime", mMysqlDateFormat.format(mObservationEnd) );
        rp.setParam( "md_period", Integer.toString(30) );
        GetMDEventListAsyncTask task = new GetMDEventListAsyncTask();
        task.execute(rp);
    }
    private class GetMDEventListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            JSONArray jsonArray, mdJson;

            if (s == null)
                return;

            Log.d("MyApp", "GetMDEventListAsyncTask replay: " + s);

            try {
                jsonArray = new JSONArray(s);
                Log.d("MyApp", "MD amount = " + jsonArray.length() );
                for( int i = 0 ; i < jsonArray.length() ; i++) {
                    mdJson = jsonArray.getJSONArray(i);
                    Segment segment = new Segment();
                    segment.eventStart = mMysqlDateFormat.parse(mdJson.getString(0));
                    segment.eventStop  = mMysqlDateFormat.parse(mdJson.getString(1));
                    mMDList.add(segment);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
            invalidate();
        }
    }
}
