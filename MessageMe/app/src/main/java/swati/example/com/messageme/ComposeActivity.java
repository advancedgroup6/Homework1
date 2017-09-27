package swati.example.com.messageme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ComposeActivity extends AppCompatActivity implements GetUserAsyncTask.UserList,GetLocAsyncTask.LocList,SendMsgAsyncTask.SendDetails {

    private static String sentTo;
    private static String regionFrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.TOKEN_PREFS, MODE_PRIVATE);
        final String username = sharedPref.getString("Username",null);
        final String token = sharedPref.getString("JWTToken",null);

        final String to = getIntent().getStringExtra("To");
        final String region = getIntent().getStringExtra("Region");
        final String majmin = getIntent().getStringExtra("MajMin");
        if(to!=null && majmin!=null){
            TextView tv1 = (TextView) findViewById(R.id.textViewTo);
            TextView tv2 = (TextView) findViewById(R.id.textViewReg);
            tv1.setText("To: "+to);
            tv2.setText("Region: "+region);
            sentTo = to;
            regionFrom = majmin;

        } else{
            ImageButton toUser = (ImageButton) findViewById(R.id.imageButtonUser);
            ImageButton location = (ImageButton) findViewById(R.id.imageButtonLoc);
            toUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetUserAsyncTask(ComposeActivity.this).execute(MainActivity.IPAdress+"getAllUsers/"+token+"/"+username);
                }
            });
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetLocAsyncTask(ComposeActivity.this).execute(MainActivity.IPAdress+"getAllRegions/"+token+"/"+username);
                }
            });
        }

        Button send = (Button) findViewById(R.id.buttonSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.editTextmsg);
                String msg = text.getText().toString();
                new SendMsgAsyncTask(ComposeActivity.this).execute(MainActivity.IPAdress+"sendMsg",username,sentTo,msg,regionFrom,token);
            }
        });

    }

    @Override
    public void setUserList(final HashMap<String, String> details) {
        ArrayList<String> userList = new ArrayList<String>(details.values());
        final ArrayList<String> keyList = new ArrayList<String>(details.keySet());
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(ComposeActivity.this,
                android.R.layout.select_dialog_item, userList);

        final Spinner sp = new Spinner(ComposeActivity.this);
        sp.setAdapter(adp);
        new AlertDialog.Builder(ComposeActivity.this)
                .setMessage("Select User")
                .setCancelable(true)
                .setView(sp)
                .show();

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d("item", (String) parent.getItemAtPosition(position));
                TextView tv1 = (TextView) findViewById(R.id.textViewTo);
                tv1.setText("To: "+parent.getItemAtPosition(position));
                sentTo = keyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void setLocList(final HashMap<String, String> details) {
        final ArrayList<String> locList = new ArrayList<String>(details.keySet());
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(ComposeActivity.this,
                android.R.layout.select_dialog_item, locList);

        final Spinner sp = new Spinner(ComposeActivity.this);
        sp.setAdapter(adp);
        new AlertDialog.Builder(ComposeActivity.this)
                .setMessage("Select Location")
                .setCancelable(true)
                .setView(sp)
                .show();

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d("item", (String) parent.getItemAtPosition(position));
                TextView tv1 = (TextView) findViewById(R.id.textViewReg);
                tv1.setText("Region: "+locList.get(position));
                regionFrom = details.get(locList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void setSentStatus(String flag) {
        if(flag!=null && flag.equals("Message Sent")){
            Toast.makeText(ComposeActivity.this,"Message sent",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ComposeActivity.this,InboxActivity.class);
            startActivity(intent);
        }

    }

    public void login(){
        Toast.makeText(ComposeActivity.this,"User session expired. Please login to continue.",Toast.LENGTH_SHORT).show();
        SharedPreferences settings = this.getSharedPreferences(MainActivity.TOKEN_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Username",null);
        prefEditor.putString("JWTToken",null);
        prefEditor.commit();
        Intent intent = new Intent(ComposeActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
