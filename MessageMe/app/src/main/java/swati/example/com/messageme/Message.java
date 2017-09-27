package swati.example.com.messageme;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by swati on 9/24/2017.
 */

public class Message implements Serializable {
    String sender;
    String msg;
    boolean isRead;
    boolean isLocked;
    String date;
    String majorminor;
    String id;
    String region;

    public static Message createMsg(JSONObject js){
        Message msg = new Message();
        try {
            msg.setDate((String) js.get("date"));
            msg.setLocked((boolean) js.get("isLocked"));
            msg.setMajorminor((String) js.get("majorminor"));
            msg.setMsg((String) js.get("msg"));
            msg.setRead((boolean) js.get("isRead"));
            msg.setSender((String) js.get("sender"));
            msg.setId((String) js.get("_id"));
            msg.setRegion((String) js.get("region"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMajorminor() {
        return majorminor;
    }

    public void setMajorminor(String majorminor) {
        this.majorminor = majorminor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
