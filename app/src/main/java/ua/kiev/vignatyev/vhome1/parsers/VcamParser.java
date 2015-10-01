package ua.kiev.vignatyev.vhome1.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.kiev.vignatyev.vhome1.models.Vcam;

/**
 * Created by vignatyev on 01.07.2015.
 */
public class VcamParser {
    public static List<Vcam> parseFeed(String JSONString){
        JSONObject obj;
        try {
            obj = new JSONObject(JSONString);
            return null;
        } catch (JSONException e) {
            try {
                JSONArray vcamArray = new JSONArray(JSONString);
                Log.d("MyApp", "Parsed: " + vcamArray.length());
                List<Vcam> vcamList = new ArrayList<>();
                for (int i = 0; i < vcamArray.length(); i++) {
                    obj = vcamArray.getJSONObject(i);
                    Vcam vcam = new Vcam();
                    if(obj.has("URL")) {
                        vcam.URL = obj.getString("URL");
                    }
                    if(obj.has("HLS")) {
                        vcam.HLS = obj.getInt("HLS");
                    }
                    if(obj.has("RTMP")) {
                        vcam.RTMP = obj.getInt("RTMP");
                    }
                    if(obj.has("I_CUSTOMER")) {
                        vcam.I_CUSTOMER = obj.getInt("I_CUSTOMER");
                    }
                    if(obj.has("I_CUSTOMER_VCAM")) {
                        vcam.I_CUSTOMER_VCAM = obj.getInt("I_CUSTOMER_VCAM");
                    }
                    if(obj.has("ROD")) {
                        vcam.ROD = obj.getInt("ROD");
                    }
                    if(obj.has("ROS")) {
                        vcam.ROS = obj.getInt("ROS");
                    }
                    if(obj.has("ON_AIR")) {
                        vcam.ON_AIR = obj.getInt("ON_AIR");
                    }
                    if(obj.has("RESTRICTION")) {
                        vcam.RESTRICTION = obj.getInt("RESTRICTION");
                    }
                    if(obj.has("EMAIL")) {
                        vcam.EMAIL = obj.getString("EMAIL");
                    }
                    if(obj.has("VENDOR_NAME")) {
                        vcam.VENDOR_NAME = obj.getString("VENDOR_NAME");
                    }
                    if(obj.has("VCAM_NAME")) {
                        vcam.VCAM_NAME = obj.getString("VCAM_NAME");
                    }
                    if(obj.has("SCHEDULE")) {
                        vcam.SCHEDULE = obj.getString("SCHEDULE");
                    }
                    if(obj.has("CUSTOMER_VCAM_NAME")) {
                        vcam.CUSTOMER_VCAM_NAME = obj.getString("CUSTOMER_VCAM_NAME");
                    }
                    if(obj.has("CUSTOMER_VCAM_LOGIN")) {
                        vcam.CUSTOMER_VCAM_LOGIN = obj.getString("CUSTOMER_VCAM_LOGIN");
                    }
                    if(obj.has("CUSTOMER_VCAM_PASSWORD")) {
                        vcam.CUSTOMER_VCAM_PASSWORD = obj.getString("CUSTOMER_VCAM_PASSWORD");
                    }
                    if(obj.has("TOKEN")) {
                        vcam.TOKEN = obj.getString("TOKEN");
                    }
                    if(obj.has("TYPE")) {
                        vcam.TYPE = obj.getString("TYPE");
                    }
                    /* SET OPTIONS */
                    if(obj.has("OPTIONS")) {
                        JSONObject options = obj.getJSONObject("OPTIONS");

                        if(obj.has("VCAM_PORT")) {
                            vcam.VCAM_PORT = options.getInt("VCAM_PORT");
                        }
                        if(obj.has("VCAM_IP")) {
                            vcam.VCAM_IP = options.getString("VCAM_IP");
                        }
                        if(obj.has("VCAM_LOCATION")) {
                            vcam.VCAM_LOCATION = options.getString("VCAM_LOCATION");
                        }
                        if(obj.has("VCAM_VIDEO")) {
                            vcam.VCAM_VIDEO = options.getString("VCAM_VIDEO");
                        }
                        if(obj.has("UTILITY_NAME")) {
                            vcam.UTILITY_NAME = options.getString("UTILITY_NAME");
                        }
                        if(obj.has("UTIL_IN_ARGS")) {
                            vcam.UTIL_IN_ARGS = options.getString("UTIL_IN_ARGS");
                        }
                        if(obj.has("VCAM_PROTOCOL")) {
                            vcam.VCAM_PROTOCOL = options.getString("VCAM_PROTOCOL");
                        }
                        if(obj.has("VCAM_AUDIO")) {
                            vcam.VCAM_AUDIO = options.getString("VCAM_AUDIO");
                        }
                        if(obj.has("ROD_START_TIME")) {
                            vcam.ROD_START_TIME = options.getInt("ROD_START_TIME");
                        }
                    }
                    /* ADD */
                    vcamList.add(vcam);
                }
                Log.d("MyApp", "VcamList length: " + vcamArray.length());
                return vcamList;
            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
