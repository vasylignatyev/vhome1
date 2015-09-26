package ua.kiev.vignatyev.vhome1.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.kiev.vignatyev.vhome1.models.Varch;

/**
 * Created by vignatyev on 17.08.2015.
 */
public class VarchParser {
    public static List<Varch> parseFeed(String JSONString) {
        List<Varch> varchList = null;
        try {
            JSONObject obj = new JSONObject(JSONString);
            if(obj.has("data")){
                varchList = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("data");
                Log.d("MyApp", "Parsed Varch: " + dataArray.length());
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONArray varchArray = dataArray.getJSONArray(i);
                    Varch varch = new Varch();
                    varch.archiveName = varchArray.getString(0);
                    varch.date = varchArray.getString(1);
                    varch.time = varchArray.getString(2);
                    varch.duration = varchArray.getString(3);
                    varch.size = varchArray.getString(4);
                    varchList.add(varch);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        return varchList;
    }
}
