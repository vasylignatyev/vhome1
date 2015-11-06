package ua.kiev.vignatyev.vhome1.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.models.MotionDetectNew;

public class MotionDetectParserNew {
    public static ArrayList<MotionDetectNew> parseFeed(ArrayList<MotionDetectNew> motionList ,
                                                       JSONArray motionArray) {
        if(motionList == null ) {
            motionList = new ArrayList<>();
        }
        try {
            Log.d("MyApp", "Parsed MotionDetect: " + motionArray.length());
            for (int i = 0; i < motionArray.length(); i++) {
                JSONObject mdElement = motionArray.getJSONObject(i);

                MotionDetectNew motionDetect = new MotionDetectNew();

                if(mdElement.has("i_motion_detect"))
                    motionDetect.iMotionDetect = mdElement.getInt("i_motion_detect");
                if(mdElement.has("issue_date"))
                    motionDetect.date = mdElement.getString("issue_date");
                if(mdElement.has("cam_name"))
                    motionDetect.cam_name = mdElement.getString("cam_name");
                if(mdElement.has("images")) {
                    JSONArray imagesArray = mdElement.getJSONArray("images");
                    motionDetect.images = new ArrayList<>();

                    for( int j=0; j < imagesArray.length(); j++ )
                        motionDetect.images.add((String) imagesArray.get(j));
                }
                motionList.add(motionDetect);
            }
        } catch (JSONException ex) {
             ex.printStackTrace();
            Log.d("MyApp", "Parser Error");
             return null;
        }
        return motionList;
    }
}
