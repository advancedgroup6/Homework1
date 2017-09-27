package swati.example.com.messageme;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * authors: Swati Jha
 */

public class GetLocAsyncTask extends AsyncTask<String, Void, HashMap<String,String>>{
    LocList activity;
    private static String errormsg;
    public GetLocAsyncTask(LocList activity) {
        this.activity = activity;
    }

    @Override
    protected HashMap<String,String> doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int statusCode = con.getResponseCode();
            if(statusCode==HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line!=null){
                    sb.append(line);
                    line = reader.readLine();
                }
                Log.d("SB:",sb.toString());
                JSONObject root = new JSONObject(sb.toString());
                String status = root.get("status").toString();
                if(status.equals("200")){
                    return AppsUtil.AppsJSONParser.parseLocations(sb.toString());
                } else if(status.equals("500")){
                    errormsg = "500";
                    return null;
                }else{
                    return null;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(HashMap<String,String> locations) {
        super.onPostExecute(locations);
        if(locations!=null){
            activity.setLocList(locations);
        } else if(errormsg.equals("500")){
            activity.login();
        }
    }



    static public interface LocList{
        public void setLocList(HashMap<String, String> details);
        public void login();
        public Context getContext();
    }
}
