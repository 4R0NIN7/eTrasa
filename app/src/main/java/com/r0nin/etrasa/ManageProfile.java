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
        buttonPassword = findViewById(R.id.buttonPassword);
        buttonName = findViewById(R.id.buttonChangeName);
        buttonBack = findViewById(R.id.buttonBackManageProfile);
        textViewEmailDisplay = findViewById(R.id.textViewEmailDisplay);
        textViewNameDisplay = findViewById(R.id.textViewNameDisplay);
        

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        textViewNameDisplay.setText(firebaseUser.getDisplayName());
        textViewEmailDisplay.setText(firebaseUser.getEmail());

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

    }



}
