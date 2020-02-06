package com.example.dogpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.dogpartner.consts.SharedPrefConstants;
import com.example.dogpartner.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import javax.annotation.Nullable;

public class UserHomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private long backpressedtime;
    private Toast backtoast;
    ImageButton addpetbutton;
    androidx.appcompat.widget.Toolbar homepagetoolbar;
    TextView nametext,emailtext,phonetext;
    String userID;
    String username,userphone;
    RecyclerView recyclerview;
    Button testbutton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        nametext = (TextView)findViewById(R.id.name_textview);
        emailtext = (TextView)findViewById(R.id.email_textview);
        phonetext = (TextView)findViewById(R.id.phone_textview);

        recyclerview = (RecyclerView)findViewById(R.id.doglist);

        testbutton = findViewById(R.id.open_test_activity_button);
        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomePageActivity.this,TestActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        homepagetoolbar = findViewById(R.id.homepagetoolbar);
        setSupportActionBar(homepagetoolbar);

        addpetbutton = (ImageButton)findViewById(R.id.addpetbutton);

        addpetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserHomePageActivity.this,AddDogActivity.class);

                startActivity(intent);

            }
        });

        //Getting user data

        userID = mAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                nametext.setText(documentSnapshot.getString("FullName"));
                emailtext.setText(documentSnapshot.getString("Email"));
                phonetext.setText(documentSnapshot.getString("Phone"));


            }
        });


//        Toast.makeText(UserHomePageActivity.this,username,Toast.LENGTH_SHORT).show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.refresh:
                Toast.makeText(UserHomePageActivity.this,"Refresh Button Clicked",Toast.LENGTH_SHORT).show();
                break;

            case R.id.signout:
                mAuth.signOut();
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("login", SharedPrefConstants.NO_LOGIN);
                editor.apply();
                startActivity(new Intent(UserHomePageActivity.this,MainActivity.class));
                finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(backpressedtime+2000>System.currentTimeMillis()){
            backtoast.cancel();
            super.onBackPressed();
            finish();
        }
        else {
            backtoast= Toast.makeText(UserHomePageActivity.this,"Press back again to exit",Toast.
                    LENGTH_SHORT);
            backtoast.show();
        }
        backpressedtime = System.currentTimeMillis();
    }
}
