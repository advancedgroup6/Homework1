package swati.example.com.messageme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swati,Rumit,Sakshi on 9/13/2017.
 */


public class AppsUtil {

    static public class AppsJSONParser{
        static ArrayList<Message> parseApps(String in) throws JSONException {
            ArrayList<Message> appsList = new ArrayList<>();
            JSONObject root = new JSONObject(in);
            //JSONArray jsArray = new JSONArray(in);
            //JSONObject root = new JSONObject(in);
            //Iterator<String> keys = root.keys();
            JSONArray appsJSONArray = new JSONArray();
            appsJSONArray = root.getJSONArray("msgList");

            for(int i=0;i<appsJSONArray.length();i++){
                JSONObject appJSONObject = appsJSONArray.getJSONObject(i);
                Message msg = Message.createMsg(appJSONObject);
                appsList.add(msg);
            }
            return appsList;
        }

        static HashMap<String, String> parseUsers(String in) throws JSONException {
            JSONObject root = new JSONObject(in);
            JSONArray appsJSONArray = new JSONArray();
            appsJSONArray = root.getJSONArray("usersList");
            HashMap<String,String> userList = new HashMap<String,String>();
            for(int i=0;i<appsJSONArray.length();i++){
                JSONObject appJSONObject = appsJSONArray.getJSONObject(i);
                String name = (String) appJSONObject.get("name");
                String userId = (String) appJSONObject.get("emailID");
                userList.put(userId,name);
            }
            return userList;
        }

        static HashMap<String, String> parseLocations(String in) throws JSONException {
            JSONObject root = new JSONObject(in);
            JSONArray appsJSONArray = new JSONArray();
            appsJSONArray = root.getJSONArray("regionList");
            HashMap<String,String> userList = new HashMap<String,String>();
            for(int i=0;i<appsJSONArray.length();i++){
                JSONObject appJSONObject = appsJSONArray.getJSONObject(i);
                String majmin = (String) appJSONObject.get("majorminor");
                String region = (String) appJSONObject.get("category");
                userList.put(region,majmin);
            }
            return userList;
        }
    }
}