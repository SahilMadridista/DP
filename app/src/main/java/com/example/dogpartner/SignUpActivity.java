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
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogpartner.consts.SharedPrefConstants;
import com.example.dogpartner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    EditText fullnameEdittext,phoneEdittext,emailEdittext,passwordEdittext,cityEdittext;
    Button registeruserbutton;
    private ProgressDialog signupProgressdialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    String userID;
    CheckBox registercheckbox;
    TextView signintext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        fullnameEdittext = (EditText)findViewById(R.id.fullnameedittext);
        phoneEdittext = (EditText)findViewById(R.id.phoneedittext);
        emailEdittext = (EditText)findViewById(R.id.emailedittext);
        passwordEdittext = (EditText)findViewById(R.id.passwordedittext);
        cityEdittext = (EditText)findViewById(R.id.cityedittext);

        signintext = (TextView)findViewById(R.id.signintext);

        signintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));

            }
        });

        registercheckbox = (CheckBox)findViewById(R.id.registercheckbox);

        signupProgressdialog = new ProgressDialog(this);
        signupProgressdialog.setCancelable(false);

        registeruserbutton = (Button)findViewById(R.id.signupbutton);

        registeruserbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();

            }
        });

        registercheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    passwordEdittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEdittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


    }

    private void registerUser() {

        final String fullname = fullnameEdittext.getText().toString().trim();
        final String phone = phoneEdittext.getText().toString().trim();
        final String email = emailEdittext.getText().toString().trim();
        final String password = passwordEdittext.getText().toString().trim();
        final String city = cityEdittext.getText().toString().trim();
        final String citylc = cityEdittext.getText().toString().toLowerCase().trim();

        if(fullname.isEmpty()){
            fullnameEdittext.setError("Name Required");
            fullnameEdittext.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            phoneEdittext.setError("Phone Number Required");
            phoneEdittext.requestFocus();
            return;
        }

        if(phone.length()!=10){
            phoneEdittext.setError("Enter a valid phone number");
            phoneEdittext.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailEdittext.setError("Email Required");
            emailEdittext.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdittext.setError("Enter a valid email address");
            emailEdittext.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordEdittext.setError("Please enter password");
            passwordEdittext.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordEdittext.setError("Password must be atleast 6 characters long");
            passwordEdittext.requestFocus();
            return;
        }

        if(city.isEmpty()){
            cityEdittext.setError("Please select a city");
            cityEdittext.requestFocus();
            return;
        }

        signupProgressdialog.setTitle("Signing Up");
        signupProgressdialog.setMessage("Just a moment...");
        signupProgressdialog.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            userID = mAuth.getCurrentUser().getUid();

                            final DocumentReference documentReference = firestore.collection("Users")
                                    .document(userID);

                            User user = new User();

                            user.FullName = fullname;
                            user.Phone = phone;
                            user.Email = email;
                            user.Password = password;
                            user.City = city;
                            user.UserID = userID;
                            user.Citylowercase = citylc;

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    signupProgressdialog.cancel();

                                    Toast.makeText(getApplicationContext(),"Profile Created",Toast.LENGTH_SHORT).show();

                                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("login", SharedPrefConstants.USER_LOGIN);
                                    editor.apply();

                                    startActivity(new Intent(SignUpActivity.this, UserHomePageActivity.class));
                                    finish();

                                }
                            });

                        }

                        else {

                            signupProgressdialog.cancel();

                            Toast.makeText(SignUpActivity.this,"Some error occured",Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    @Override
    protected void onStart() {

        super.onStart();

        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(SignUpActivity.this,UserHomePageActivity.class));
            finish();
        }
    }


}
