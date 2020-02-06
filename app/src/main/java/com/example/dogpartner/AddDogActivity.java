package com.example.dogpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.dogpartner.model.Dog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddDogActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar addogtoolbar;
    Spinner genderspinner;
    EditText Name,Breed,Age;
    Button adddogdetails;
    ImageButton addphoto;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    final int RequestPermissionCode =1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ImageView dogimage;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    final int CROP_PIC = 2;
    private Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding your dog...");


        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReference();
        firestore = FirebaseFirestore.getInstance();

        addogtoolbar = findViewById(R.id.adddogtoolbar);
        setSupportActionBar(addogtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add your Dog");


        Name = findViewById(R.id.dog_name_edittext);
        Age = findViewById(R.id.dog_age_edittext);
        Breed = findViewById(R.id.dog_breed_edittext);

        dogimage = (ImageView)findViewById(R.id.uploadingimage);

        genderspinner = (Spinner)findViewById(R.id.genderspinner);

        addphoto = (ImageButton)findViewById(R.id.uploadbutton);

        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCamera();

            }
        });

        adddogdetails = (Button)findViewById(R.id.adddogbutton);

        adddogdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // uploadImage();
                uploadDetails();

            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderspinner.setAdapter(adapter);

        int permissionCheck = ContextCompat.checkSelfPermission(AddDogActivity.this,Manifest.permission.CAMERA);
        if(permissionCheck ==PackageManager.PERMISSION_DENIED){
            RequestRuntimePermission();
        }

    }

    /*private void uploadImage() {

        final Dog dog = new Dog();

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");

        StorageReference storageReference = mStorageRef.child(System.currentTimeMillis()
                + "." +"jpg");

        storageReference.putFile(picUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(AddDogActivity.this,"Photo Uploaded",Toast.LENGTH_SHORT).show();

                        dog.dogimageURI = picUri;


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AddDogActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }*/

    private void RequestRuntimePermission() {

        if(ActivityCompat.shouldShowRequestPermissionRationale(AddDogActivity.this,Manifest.permission.CAMERA)){

            Toast.makeText(AddDogActivity.this,"Camera permission allowed",Toast.LENGTH_SHORT).show();

        }
        else {

            ActivityCompat.requestPermissions(AddDogActivity.this,
                    new String[]{Manifest.permission.CAMERA},RequestPermissionCode);

        }

    }

    private void openCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);

            }
            else
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }



    }

    private void uploadDetails() {

        String dogname = Name.getText().toString().trim();
        String breed = Breed.getText().toString().trim();
        String age = Age.getText().toString().trim();
        String gender = genderspinner.getSelectedItem().toString().trim();

        if(dogname.isEmpty()){
            Name.setError("Enter Dog's Name");
            Name.requestFocus();
            return;
        }

        if(breed.isEmpty()){
            Breed.setError("Enter Dog's breed");
            Breed.requestFocus();
            return;
        }

        if(age.isEmpty()){
            Age.setError("Enter Dog's age");
            Age.requestFocus();
            return;
        }

        if(gender == "Select Gender"){
            Toast.makeText(AddDogActivity.this,"Please select your dog's gender",Toast.LENGTH_SHORT).show();
            return;
        }

//        if(dogimage.getDrawable() == null){
//            Toast.makeText(AddDogActivity.this,"Please capture an image",Toast.LENGTH_SHORT).show();
//            return;
//        }

        final Dog dog = new Dog();

        dog.dog_name = dogname;
        dog.dog_breed = breed;
        dog.dog_age = age;
        dog.dog_gender = gender;

        progressDialog.show();

        final DocumentReference ref = firestore.collection("Dogs").document();
        dog.dog_ID = ref.getId();

        ref.set(dog).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                String userID = mAuth.getCurrentUser().getUid();

                firestore.collection("Users").document(userID)
                        .update("DogsID", FieldValue.arrayUnion(ref.getId()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(AddDogActivity.this,"Stored in user list",Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                                startActivity(new Intent(AddDogActivity.this,UserHomePageActivity.class));
                                finish();
                            }
                        });

            }
        });

    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
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

}
