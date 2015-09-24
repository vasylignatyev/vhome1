package ua.kiev.vignatyev.vhome1;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vignatyev on 19.06.2015.
 */
public class HTTPManager {

    public static String getDataOld(String uri){
        AndroidHttpClient client = AndroidHttpClient.newInstance("AndridAgent");
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        try {
            response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            client.close();
        }
    }
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
