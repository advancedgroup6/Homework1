package swati.example.com.messageme;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * authors: Swati Jha
 */

public class SendMsgAsyncTask extends AsyncTask<String, Void, String>{
    SendDetails activity;
    private static String errormsg;
    public SendMsgAsyncTask(SendDetails activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            URL url = new URL(params[0]);
            String sender = params[1];
            String receiver = params[2];
            String msg = params[3];
            String region = params[4];
            String token = params[5];
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("sender", sender)
                    .appendQueryParameter("receiver", receiver)
                    .appendQueryParameter("msg", msg)
                    .appendQueryParameter("majorminor", region)
                    .appendQueryParameter("token", token);
            String query = builder.build().getEncodedQuery();

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int statusCode = con.getResponseCode();
            if(statusCode==HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line!=null){
                    sb.append(line);
                    line = reader.readLine();
                }
                JSONObject root = new JSONObject(sb.toString());
                Log.d("SB:",root.get("status").toString());
                String status = root.get("status").toString();
                if(status.equals("200")){
                    return "Message Sent";
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
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
        if(msg!=null){
            activity.setSentStatus(msg);
        } else if(errormsg.equals("500")){
            activity.login();
        }

    }



    static public interface SendDetails{
        public void setSentStatus(String msg);
        public void login();
        public Context getContext();
        //boolean onMenuItemClick(MenuItem item);
    }
}
