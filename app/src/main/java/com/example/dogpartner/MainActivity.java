package com.example.dogpartner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dogpartner.consts.SharedPrefConstants;

public class MainActivity extends AppCompatActivity {

    Button continuebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        final int loginStatus = preferences.getInt("login", SharedPrefConstants.NO_LOGIN);

        continuebutton = (Button)findViewById(R.id.continuebutton);

        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginStatus == SharedPrefConstants.USER_LOGIN) {
                    startActivity(new Intent(MainActivity.this,UserHomePageActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                    finish();
                }
            }
        });



    }
}
