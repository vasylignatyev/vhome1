package ua.kiev.vignatyev.vhome1.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.kiev.vignatyev.vhome1.models.MotionDetect;
import ua.kiev.vignatyev.vhome1.models.MotionDetectEvent;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MotionDetectEventParser {
    /**
     *
     * @param s JSON String
     * @return
     */
    public static MotionDetectEvent parseFeed(String s) {
        MotionDetectEvent motionDetectEvent = null;
        try {
            JSONObject obj = new JSONObject(s);
            //Log.d("MyApp", obj.toString());
            motionDetectEvent = new MotionDetectEvent();
            if(obj.has("images")) {
                ArrayList<String> images = new ArrayList<>();

                JSONArray imagesArray = obj.getJSONArray("images");
                for (int i = 0; i < imagesArray.length(); i++) {
                    //Log.d("MyApp", imagesArray.getString(i));
                    images.add(imagesArray.getString(i));
                }
                motionDetectEvent.images = images;
            }
            if(obj.has("video"))
                motionDetectEvent.video = obj.getString("video");
            if(obj.has("text")) {
                JSONObject text = obj.getJSONObject("text");
                if(text.has("I_MOTION_DETECT"))
                    motionDetectEvent.iMotionDetect = text.getInt("I_MOTION_DETECT");
                if(text.has("DIR_NAME"))
                    motionDetectEvent.dirName = text.getString("DIR_NAME");
                if(text.has("CAM_TOKEN"))
                    motionDetectEvent.camToken = text.getString("CAM_TOKEN");
                if(text.has("EVENT_DATE"))
                    motionDetectEvent.date = text.getString("EVENT_DATE");
                if(text.has("CAM_NAME"))
                    motionDetectEvent.camName = text.getString("CAM_NAME");
                if(text.has("URL"))
                    motionDetectEvent.url = text.getString("URL");
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            return motionDetectEvent;
        }
    }
}
