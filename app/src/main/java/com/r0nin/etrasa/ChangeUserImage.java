package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChangeUserImage extends AppCompatActivity {

    protected Button buttonImageGoBack, buttonSaveImage, buttonChooseImage;
    protected ImageView imageViewUserImage;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "ChangeUserImage";
    protected Uri uri;
    protected final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    protected StorageReference mStorageRef;
    private String imageFilePath;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_image);

        buttonImageGoBack = findViewById(R.id.buttonImageGoBack);
        buttonSaveImage  = findViewById(R.id.buttonSaveImage);
        buttonSaveImage.setVisibility(View.GONE);
        buttonChooseImage = findViewById(R.id.buttonSelectImage);
        imageViewUserImage = findViewById(R.id.imageViewUserImage);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        buttonImageGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ManageProfile.class);
                startActivity(intent);
                finish();
            }
        });
        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        Log.i(TAG,"createImageFile onSuccess");
                    } catch (IOException ex) {
                    }
                    if(photoFile != null){
                        uri = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.fileprovider",
                                photoFile);
                        Log.i(TAG,"Capture image uri" + uri);
                        Log.i(TAG,"Capture image");
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
        progressDialog = new ProgressDialog(ChangeUserImage.this);
        progressDialog.setTitle(R.string.progress_bar);


        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveToDB();

            }
        });



    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }



    private void saveToDB(){
        progressDialog.show();
        if(uri != null) {
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = mStorageRef.child("userImages");
            final StorageReference userRef = imagesRef.child(firebaseUser.getUid());
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final String filename = firebaseUser.getUid() + "_" + timeStamp;
            final StorageReference fileRef = userRef.child(filename);
            Log.i(TAG, "Ref created: onSuccess");
            progressDialog.show();
            /*
            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Intent intent = new Intent(getApplicationContext(),ManageProfile.class);
                            Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            String key = database.child("userImages").push().getKey();
                            Image image = new Image(key, firebaseUser.getUid(), firebaseUri.toString());
                            database.child("userImages").setValue(image);
                            Uri uri = fileRef.getDownloadUrl();
                            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.upload_success), Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "saveToDB onSuccess");
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.upload_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.i(TAG, "saveToDB onFailure");
                            Intent intent = new Intent(getApplicationContext(), ManageProfile.class);
                            startActivity(intent);
                            finish();
                        }
                    });
             */
            UploadTask uploadTask = fileRef.putFile(uri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        final Uri downloadUri = task.getResult();
                        String key = database.child("userImages").push().getKey();
                        Image image = new Image(key, firebaseUser.getUid(), downloadUri.toString());
                        database.child("userImages").child(firebaseUser.getUid()).setValue(image);
                        Log.i(TAG, "saveToDB onSuccess");
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                        firebaseUser.updateProfile(profileUpdates);
                        progressDialog.dismiss();
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.upload_success), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();

                    } else {
                        Log.i(TAG, "saveToDB onFailure");
                        Intent intent = new Intent(getApplicationContext(), ManageProfile.class);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.upload_failed), Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    imageViewUserImage.setImageBitmap(rotateImage(bitmap,(float)270));
                    buttonSaveImage.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

}
