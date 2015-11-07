package ua.kiev.vignatyev.vhome1.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ua.kiev.vignatyev.vhome1.models.VarchData;

/**
 * Created by vignatyev on 04.11.2015.
 */
public class VarchDataParser {
    public static List<VarchData> parseFeed(String JSONString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        List<VarchData> varchDataList = null;
        try {
            JSONObject obj = new JSONObject(JSONString);
            if(obj.has("data")){
                varchDataList = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("data");
                Log.d("MyApp", "Parsed Varch: " + dataArray.length());
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject varchObject = dataArray.getJSONObject(i);
                    VarchData varchData = new VarchData();
                    if(varchObject.has("NAME"))
                        varchData.archiveName = varchObject.getString("NAME");
                    if(varchObject.has("ISSUE_DATE")) {
                        varchData.issueDate = Calendar.getInstance();
                        try {
                            varchData.issueDate.setTime(sdf.parse(varchObject.getString("ISSUE_DATE")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if(varchObject.has("DURATION"))
                        varchData.duration = varchObject.getInt("DURATION");
                    if(varchObject.has("SIZE"))
                        varchData.size = varchObject.getLong("SIZE");
                    varchDataList.add(varchData);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        return varchDataList;
    }
}
