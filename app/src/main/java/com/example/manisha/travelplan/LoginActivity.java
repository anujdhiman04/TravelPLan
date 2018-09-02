package com.example.manisha.travelplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText user_name, password;

    private DBHelper mydb;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name = (EditText)findViewById(R.id.user_name);
        password = (EditText)findViewById(R.id.password);
        mydb = new DBHelper(this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if(pref.contains("user_name")){
            login(pref.getString(Constants.user_name,""),pref.getString(Constants.password,""));
        }

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    //login
                    login(user_name.getText().toString(), password.getText().toString());
                }
            }
        });

    }

    public void login(String user_name, String pass){
        int authorised = mydb.insertUser(user_name, pass);
        switch (authorised){
            case 1:
                // Authoerised and do login
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.user_name, user_name);
                editor.putString(Constants.password, pass);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                startActivity(intent);
                finish();
                break;
            case 2:
                //password didnt match
                password.setError("Incorrect Password");
                Toast.makeText(LoginActivity.this, "Incorrect Password, Try again", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean validate() {
        boolean validate = true;
        String username = user_name.getText().toString();
        String pass = password.getText().toString();

        if(TextUtils.isEmpty(username)){
            user_name.setError("Required field");
            validate = false;
        }else {
            user_name.setError(null);
        }

        if(TextUtils.isEmpty(pass)){
            password.setError("Required field");
            validate = false;
        }else {
            if (pass.length()<6){
                password.setError("Password must contain atleast 6 characters");
                validate = false;
            }else {
                user_name.setError(null);
            }
        }
        return validate;
    }
}
