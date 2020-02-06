package com.example.dogpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.example.dogpartner.consts.SharedPrefConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar signintoolbar;
    EditText signinemail,signinpassword;
    ProgressDialog signinProgressdialog;
    Button signinButton;
    CheckBox signincheckbox;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        signintoolbar = findViewById(R.id.signintoolbar);
        setSupportActionBar(signintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signinemail = (EditText)findViewById(R.id.emailedittextsignin);
        signinpassword = (EditText)findViewById(R.id.passwordedittextsignin);

        signincheckbox = (CheckBox)findViewById(R.id.signincheckbox);

        signincheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    signinpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    signinpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        signinButton = (Button) findViewById(R.id.signinbutton);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInUser();

            }
        });

        signinProgressdialog = new ProgressDialog(this);
        signinProgressdialog.setCancelable(false);



    }

    private void signInUser() {

        String email = signinemail.getText().toString().trim();
        String password = signinpassword.getText().toString().trim();

        if(email.isEmpty()){
            signinemail.setError("Please enter email");
            signinemail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signinemail.setError("Enter a valid email address");
            signinemail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            signinpassword.setError("Please enter password");
            signinpassword.requestFocus();
            return;
        }

        if(password.length()<6){
            signinpassword.setError("Password must be minimum 6 characters long");
            signinpassword.requestFocus();
            return;
        }

        signinProgressdialog.setTitle("Logging in");
        signinProgressdialog.setMessage("It will take a moment...");
        signinProgressdialog.show();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    signinProgressdialog.dismiss();

                    SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("login", SharedPrefConstants.USER_LOGIN);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), UserHomePageActivity.class));

                    finish();

                }

                else {

                    Toast.makeText(SignInActivity.this,"Some error happened",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}
