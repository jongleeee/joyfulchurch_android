package com.mysampleapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.mysampleapp.util.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText myPassword;
    private Button enter;
    protected Context context;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this.getApplicationContext();
        user = new User(context);
        myPassword = (EditText) findViewById(R.id.myPassword);
        enter = (Button) findViewById(R.id.enter);
        enter.setOnClickListener(this);
    }

    public void onClick(View view) {
        String pw = myPassword.getText().toString();

        if (pw.equals("설교")) {
            user.updateAuthorization("설교");
        }
        else if (pw.equals("광고")) {
            user.updateAuthorization("광고");
        }
        else if (pw.equals("joyfulAdmin")) {
            user.updateAuthorization("Admin");
        }
        Toast toast3 = Toast.makeText(this, getString(R.string.correct_password), Toast.LENGTH_SHORT);
        toast3.show();
    }
}
