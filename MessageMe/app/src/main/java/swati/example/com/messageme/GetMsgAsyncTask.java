package swati.example.com.messageme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * authors: Swati Jha
 */

public class GetMsgAsyncTask extends AsyncTask<String, Void, ArrayList<Message>>{
    MessageDetails activity;
    private static String errormsg;

    public GetMsgAsyncTask(MessageDetails activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Message> doInBackground(String... params) {

        try {

            URL url = new URL(params[0]);
            String receiver = params[1];
            String token = params[2];
            String method = params[3];
            if(method!=null && method.equals("updateMsgs")){
                    String region = params[4];
                    url = new URL(params[0]+"updateMsgs/"+receiver+"/"+region+"/"+token);
                } else{
                    url = new URL(params[0]+receiver+"/"+token);
                }
            Log.d("URL:",url.toString());
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
                    Log.d("SB:",root.get("status").toString());
                    String status = root.get("status").toString();
                    if(status.equals("200")){
                        return AppsUtil.AppsJSONParser.parseApps(sb.toString());
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
    protected void onPostExecute(ArrayList<Message> apps) {
        super.onPostExecute(apps);
        if(apps!=null){
            activity.setMessageDetails(apps);
            //Log.d("demo", apps.toString());
        } else if(errormsg.equals("500")){
            activity.login();
        }
    }



    static public interface MessageDetails{
        public void setMessageDetails(ArrayList<Message> details);
        public void login();
        public Context getContext();
        //boolean onMenuItemClick(MenuItem item);
    }
}
