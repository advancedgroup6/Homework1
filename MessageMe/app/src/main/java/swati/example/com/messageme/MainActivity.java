package swati.example.com.messageme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoginAsyncTask.UserDetails {

    public static final String TOKEN_PREFS = "TokenPrefs";
    public static final String IPAdress = "http://13.59.149.10:8080/";
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getSharedPreferences(TOKEN_PREFS, MODE_PRIVATE);
        String username = sharedPref.getString("Username",null);
        String token = sharedPref.getString("JWTToken",null);
        if(username!=null){
            Intent intent = new Intent(MainActivity.this,InboxActivity.class);
            startActivity(intent);
        }
            Button btnLogin = (Button) findViewById(R.id.buttonLogin);
            Button btnNewUser = (Button) findViewById(R.id.buttonNewUser);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(MainActivity.this);
                    pd.setCancelable(false);
                    pd.show();
                    EditText username = (EditText) findViewById(R.id.editTextUserName);
                    EditText password = (EditText) findViewById(R.id.editTextPassword);
                    Log.d("Username123:",username.getText().toString());
                    if(username.getText()!=null && !username.getText().toString().trim().isEmpty() &&
                            password.getText()!=null && !password.getText().toString().trim().isEmpty() ){
                        new LoginAsyncTask(MainActivity.this).execute(IPAdress+"login"
                                ,username.getText().toString(),password.getText().toString());
                    } else{
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Username and password cannot be empty.",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            btnNewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                    startActivity(intent);
                }
            });
        }

    @Override
    public void setUserDetails(User user) {
        pd.dismiss();
        if(user!=null){
            SharedPreferences settings = getSharedPreferences(TOKEN_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putString("Username",user.getName());
            prefEditor.putString("JWTToken",user.getJwtToken());
            prefEditor.commit();
            Toast.makeText(MainActivity.this,"User successfully logged in.",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,InboxActivity.class);
            startActivity(intent);
        } else{
            Toast.makeText(MainActivity.this,"Login failed. Invalid password.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Context getContext() {
        return null;
    }
}
