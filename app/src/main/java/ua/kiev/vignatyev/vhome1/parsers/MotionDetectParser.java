package ua.kiev.vignatyev.vhome1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.models.MotionDetect;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MotionDetectParser {
    public static ArrayList<MotionDetect> parseFeed(String JSONString) {
        ArrayList<MotionDetect> dataList = null;
        try {
            JSONObject obj = new JSONObject(JSONString);
            if(obj.has("data")){
                dataList = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("data");
                Log.d("MyApp", "Parsed MotionDetect: " + dataArray.length());
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONArray dataElement = dataArray.getJSONArray(i);
                    MotionDetect motionDetect = new MotionDetect( dataElement.getInt(0) );
                    //motionDetect.iMotionDetect = dataElement.getInt(0);
                    motionDetect.date = dataElement.getString(1);
                    motionDetect.camName = dataElement.getString(2);
                    dataList.add(motionDetect);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            return dataList;
        }
    }
}
