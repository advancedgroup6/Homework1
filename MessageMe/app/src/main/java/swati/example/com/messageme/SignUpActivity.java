package swati.example.com.messageme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity implements SignUpAsyncTask.UserDetails {

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUp = (Button) findViewById(R.id.buttonSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(SignUpActivity.this);
                pd.setCancelable(false);
                pd.show();
                EditText firstName =  (EditText) findViewById(R.id.editTextfName);
                EditText lastName = (EditText) findViewById(R.id.editTextlname);
                EditText username = (EditText) findViewById(R.id.editTextuName);
                EditText password = (EditText) findViewById(R.id.editTextpwd);
                EditText confirmPassword = (EditText) findViewById(R.id.editTextcPwd);
                if(firstName.getText()!=null && !firstName.getText().toString().trim().isEmpty() &&
                        lastName.getText()!=null && !lastName.getText().toString().trim().isEmpty() &&
                        username.getText()!=null && !username.getText().toString().trim().isEmpty() &&
                        password.getText()!=null && !password.getText().toString().trim().isEmpty() &&
                        confirmPassword.getText()!=null && !confirmPassword.getText().toString().trim().isEmpty() ){
                        String pwd = password.getText().toString();
                        String cpwd = confirmPassword.getText().toString();
                        if(pwd.equals(cpwd)){
                            String name = firstName.getText().toString() + lastName.getText().toString();
                             new SignUpAsyncTask(SignUpActivity.this).execute(MainActivity.IPAdress+"signup"
                               ,username.getText().toString(),pwd,name);
                        } else{
                            pd.dismiss();
                            Toast.makeText(SignUpActivity.this,"Passwords do not match.",Toast.LENGTH_SHORT).show();

                        }

                } else{
                    pd.dismiss();
                    Toast.makeText(SignUpActivity.this,"Please fill out all the details.",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void setUserDetails(User user) {
        pd.dismiss();
        if(user!=null){
            SharedPreferences settings = getSharedPreferences(MainActivity.TOKEN_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putString("Username",user.getName());
            prefEditor.putString("JWTToken",user.getJwtToken());
            prefEditor.commit();
            Toast.makeText(SignUpActivity.this,"User successfully logged in.",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this,InboxActivity.class);
            startActivity(intent);
        } else{
            Toast.makeText(SignUpActivity.this,"User already exists!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Context getContext() {
        return null;
    }
}
