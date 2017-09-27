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
import java.util.ArrayList;

/**
 * authors: Swati Jha
 */

public class LoginAsyncTask extends AsyncTask<String, Void, User>{
    UserDetails activity;

    public LoginAsyncTask(UserDetails activity) {
        this.activity = activity;
    }

    @Override
    protected User doInBackground(String... params) {

        try {

            URL url = new URL(params[0]);
            String username = params[1];
            String password = params[2];
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("emailID", username)
                    .appendQueryParameter("password", password);
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
                Log.d("SB:",sb.toString());
                JSONObject root = new JSONObject(sb.toString());
                String status = root.get("status").toString();
                if(status.equals("200")){
                    User user = new User();
                    JSONObject js = (JSONObject) root.get("userDetails");
                    user.setName((String)js.get("emailID"));
                    user.setJwtToken((String)root.get("token"));
                    return user;
                } else{
                    return null;
                }


            } else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        if(user!=null){
            activity.setUserDetails(user);
            //Log.d("demo", apps.toString());
        }
    }



    static public interface UserDetails{
        public void setUserDetails(User user);
        public Context getContext();
        //boolean onMenuItemClick(MenuItem item);
    }
}
