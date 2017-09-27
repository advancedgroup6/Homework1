package swati.example.com.messageme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadMsgActivity extends AppCompatActivity implements DeleteAsyncTask.UserList {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_msg);

        final Message msg = (Message) getIntent().getExtras().getSerializable("message");

        TextView from = (TextView) findViewById(R.id.textViewFrom);
        from.setText("From: "+msg.getSender());
        TextView region = (TextView) findViewById(R.id.textViewRegion);
        region.setText("Region: "+msg.getRegion());
        TextView message = (TextView) findViewById(R.id.textViewMessage);
        message.setText(msg.getMsg());

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.TOKEN_PREFS, MODE_PRIVATE);
        final String username = sharedPref.getString("Username",null);
        final String token = sharedPref.getString("JWTToken",null);

        new DeleteAsyncTask(ReadMsgActivity.this).execute(MainActivity.IPAdress+"updateMsgRead/"+msg.getId()+"/"+token+"/"+username,"read");

        ImageButton deleteBtn = (ImageButton) findViewById(R.id.btnDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteAsyncTask(ReadMsgActivity.this).execute(MainActivity.IPAdress+"deleteMsg/"+msg.getId()+"/"+token+"/"+username,"delete");
            }
        });

        ImageButton replyBtn = (ImageButton) findViewById(R.id.btnReply);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadMsgActivity.this,ComposeActivity.class);
                intent.putExtra("To",msg.getSender());
                intent.putExtra("Region",msg.getRegion());
                intent.putExtra("MajMin",msg.getMajorminor());
                startActivity(intent);
            }
        });

    }

    @Override
    public void setStatus(String flag) {
        if(flag.equals("deleted")){
            Intent intent = new Intent(ReadMsgActivity.this,InboxActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public Context getContext() {
        return null;
    }

    public void login(){
        Toast.makeText(ReadMsgActivity.this,"User session expired. Please login to continue.",Toast.LENGTH_SHORT).show();
        SharedPreferences settings = this.getSharedPreferences(MainActivity.TOKEN_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Username",null);
        prefEditor.putString("JWTToken",null);
        prefEditor.commit();
        Intent intent = new Intent(ReadMsgActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
