package ua.kiev.vignatyev.vhome1.models;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MotionDetect {
    public int iMotionDetect;
    public String date;
    public String camName;

    //public MotionDetectEvent motionDetectEvent;
    public List<String> images;
    public String video;
    public String camToken;
    public String dirName;
    public String url;

    public Bitmap thumb;

    public MotionDetect(int iMotionDetect) {
        //Log.d("MyApp", "MotionDetect ID : " + iMotionDetect);
        this.iMotionDetect = iMotionDetect;
    }

    /**
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
       // Log.d("MyApp", "MotionDetect");
        if (!(o instanceof MotionDetect)) {
            return false;
        }
        MotionDetect other = (MotionDetect) o;
      //  Log.d("MyApp", "Compare " + other.iMotionDetect + " to " + iMotionDetect);
        return iMotionDetect == other.iMotionDetect;
    }
    public int hashCode() {
        int hCode = new Integer(iMotionDetect).hashCode();
        return (hCode);
    }

    /**
     *
     * @param motionDetectEvent
     */
    public void setEventParams(MotionDetectEvent motionDetectEvent){
        this.images = motionDetectEvent.images;
        this.video = motionDetectEvent.video;
        //Log.d("MyApp", "Video : " + this.video);
        this.camToken = motionDetectEvent.camToken;
        this.dirName = motionDetectEvent.dirName;
        this.url = motionDetectEvent.url;
    }

}