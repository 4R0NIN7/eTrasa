package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;


public class ManageProfile extends AppCompatActivity {

    protected Button buttonChangeImage, buttonEmail, buttonPassword, buttonBack, buttonName;
    protected TextView textViewNameDisplay, textViewEmailDisplay;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    protected DatabaseReference dbImages = FirebaseDatabase.getInstance().getReference("/userImages");
    protected ImageView imageView;
    private static final String TAG = "ManageProfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        buttonEmail = findViewById(R.id.buttonEmail);
        buttonPassword = findViewById(R.id.buttonPassword);
        buttonName = findViewById(R.id.buttonChangeName);
        buttonBack = findViewById(R.id.buttonBackManageProfile);
        textViewEmailDisplay = findViewById(R.id.textViewEmailDisplay);
        textViewNameDisplay = findViewById(R.id.textViewNameDisplay);
        buttonChangeImage =findViewById(R.id.buttonChangeImage);
        imageView = findViewById(R.id.imageViewManageProfile);
        textViewNameDisplay.setText(firebaseUser.getDisplayName());
        textViewEmailDisplay.setText(firebaseUser.getEmail());




        buttonChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageProfile.this,ChangeUserImage.class);
                startActivity(intent);
                finish();
            }
        });

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProfile.this,ChangeEmail.class);
                startActivity(intent);
                finish();
            }
        });
        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProfile.this,ChangePassword.class);
                startActivity(intent);
                finish();
            }
        });
        buttonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProfile.this,ChangeName.class);
                startActivity(intent);
                finish();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageProfile.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        dbImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Image image = dataSnapshot.getValue(Image.class);
                if(image == null) {
                    Log.i(TAG, "ManageProfile image == null");
                    Toast.makeText(getApplicationContext(), "There is no image for you", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!image.downloadUrl.equals("")){
                    Log.i(TAG, "ManageProfile !image.downloadUrl.equals(\"\")" + image.downloadUrl);
                    Glide.with(ManageProfile.this)
                            .load(firebaseUser.getPhotoUrl())
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
        if(firebaseUser.getPhotoUrl() != null)
            Glide.with(ManageProfile.this)
                    .load(firebaseUser.getPhotoUrl().toString())
                    .into(imageView);
    }
}

