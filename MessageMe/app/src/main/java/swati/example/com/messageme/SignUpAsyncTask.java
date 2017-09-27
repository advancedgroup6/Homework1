package swati.example.com.messageme;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class SignUpAsyncTask extends AsyncTask<String, Void, User>{
    UserDetails activity;

    public SignUpAsyncTask(UserDetails activity) {
        this.activity = activity;
    }

    @Override
    protected User doInBackground(String... params) {

        try {

            URL url = new URL(params[0]);
            String username = params[1];
            String password = params[2];
            String name = params[3];
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("emailID", username)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("address", "add")
                    .appendQueryParameter("phoneNo", "123");
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
                Log.d("SB:",root.get("status").toString());
                String status = root.get("status").toString();
                if(status.equals("200")){
                    Log.d("Inside:",root.get("status").toString());
                    User user = new User();
                    JSONObject js = (JSONObject) root.get("userDetails");
                    user.setName((String)js.get("emailID"));
                    user.setJwtToken((String)root.get("token"));
                    return user;
                } else{
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
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        activity.setUserDetails(user);
    }



    static public interface UserDetails{
        public void setUserDetails(User user);
        public Context getContext();
        //boolean onMenuItemClick(MenuItem item);
    }
}
