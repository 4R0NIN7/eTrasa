package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


/*

Dorobić change profile żeby zmieniał obrazek -> trzeba użyć firebase storage i wykorzystać kamere (to już robiłem i było proste)
Dorobić zmiane haseł
Termin tego
1. Dzis wiczorem
2. Jutro jak zrobie pso


 */


public class ManageProfile extends AppCompatActivity {

    protected Button buttonImage, buttonEmail, buttonPassword, buttonSave;
    protected TextView textViewNameDisplay, textViewEmailDisplay;
    protected ImageView imageViewImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        buttonEmail = findViewById(R.id.buttonEmail);
        buttonImage = findViewById(R.id.buttonImage);
        buttonPassword = findViewById(R.id.buttonPassword);
        buttonSave = findViewById(R.id.buttonChangeName);
        textViewEmailDisplay = findViewById(R.id.textViewEmailDisplay);
        textViewNameDisplay = findViewById(R.id.textViewNameDisplay);
        imageViewImage = findViewById(R.id.imageViewImage);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        textViewNameDisplay.setText(firebaseUser.getDisplayName());
        textViewEmailDisplay.setText(firebaseUser.getEmail());
    }


    private void updateName(FirebaseUser firebaseUser){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textViewNameDisplay.getText().toString()).build();
        firebaseUser.updateProfile(profileUpdates);
    }


    private void updateImage(){

    }


}
