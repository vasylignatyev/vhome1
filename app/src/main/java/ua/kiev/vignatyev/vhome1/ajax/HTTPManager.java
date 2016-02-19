package ua.kiev.vignatyev.vhome1.ajax;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vignatyev on 19.06.2015.
 */
public class HTTPManager {

    public static String getData(RequestPackage p){
        BufferedReader reader = null;
        String uri = p.getUri();
        if(p.getMethod().equals("GET")){
            uri += "?" + p.getEncodedParams();
        }
        Log.d("MyApp", "HTTPManager URI: " + uri);
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod());

            if(p.getMethod().equals("POST")){
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(p.getEncodedParams());
                writer.flush();
            }

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line= reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            String content = sb.toString();
            //Log.d("MyApp", "Replay conten: " + content);
            return content;
        } catch (Exception e) {
            Log.d("MyApp", "Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if(reader != null) try {
                reader.close();
            } catch (Exception e) {
                Log.d("MyApp", "Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
}
