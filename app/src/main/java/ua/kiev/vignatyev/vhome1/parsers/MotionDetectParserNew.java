package ua.kiev.vignatyev.vhome1.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.models.MotionDetect;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MotionDetectParserNew {
    public static ArrayList<MotionDetect> parseFeed(String JSONString) {
        ArrayList<MotionDetect> motionList = null;
        try {
            motionList = new ArrayList<>();
            JSONArray motionArray = obj.getJSONArray("data");
            Log.d("MyApp", "Parsed MotionDetect: " + motionArray.length());
            for (int i = 0; i < motionArray.length(); i++) {


                JSONArray dataElement = motionArray.getJSONArray(i);
                MotionDetect motionDetect = new MotionDetect( dataElement.getInt(0) );
                motionDetect.date = dataElement.getString(1);
                motionDetect.camName = dataElement.getString(2);
                motionList.add(motionDetect);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            return motionList;
        }
    }
}
