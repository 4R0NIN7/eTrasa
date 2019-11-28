package com.r0nin.etrasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ManageProfile extends AppCompatActivity {

    protected Button buttonImage, buttonEmail, buttonPassword, buttonBack, buttonName;
    protected TextView textViewNameDisplay, textViewEmailDisplay;
    protected ImageView imageViewImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        buttonEmail = findViewById(R.id.buttonEmail);
        buttonImage = findViewById(R.id.buttonImage);
        buttonPassword = findViewById(R.id.buttonPassword);
        buttonName = findViewById(R.id.buttonChangeName);
        buttonBack = findViewById(R.id.buttonBack);
        textViewEmailDisplay = findViewById(R.id.textViewEmailDisplay);
        textViewNameDisplay = findViewById(R.id.textViewNameDisplay);
        imageViewImage = findViewById(R.id.imageViewImage);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        textViewNameDisplay.setText(firebaseUser.getDisplayName());
        textViewEmailDisplay.setText(firebaseUser.getEmail());

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProfile.this,ChangeEmail.class);
                startActivity(intent);
            }
        });
        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProfile.this,ChangePassword.class);
                startActivity(intent);
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
    }



}
