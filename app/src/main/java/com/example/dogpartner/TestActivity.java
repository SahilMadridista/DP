package com.example.dogpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.dogpartner.model.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class TestActivity extends AppCompatActivity {

    ImageView testimage;
    Button opencamera,uploadphoto;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private Uri picUri;
    ImageView dogimage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        opencamera = findViewById(R.id.cameraopen);
        uploadphoto = findViewById(R.id.uploadphoto);

        dogimage = findViewById(R.id.imageview);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding photo...");

        opencamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCamera();

            }
        });

        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadPhoto();

            }
        });

    }

    private void openCamera(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            picUri = data.getData();
            dogimage.setImageBitmap(photo);
        }
    }



    private void uploadPhoto() {

        if(picUri != null){

            progressDialog.show();
            StorageReference reference = storageReference.child("images/"+ UUID.randomUUID().toString());

            reference.putFile(picUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Uri downloadUrl = StorageReference.getDownloadUrl().getResult();


                            progressDialog.cancel();
                            Toast.makeText(TestActivity.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TestActivity.this,UserHomePageActivity.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.cancel();
                    Toast.makeText(TestActivity.this,"Image Uploading failed",Toast.LENGTH_SHORT).show();

                }
            });

        }

    }
}
