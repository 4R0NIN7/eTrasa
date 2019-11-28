package com.r0nin.etrasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ChangeName extends AppCompatActivity {

    protected EditText editTextName;
    protected Button buttonSave;
    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        buttonSave = findViewById(R.id.buttonSave);
        editTextName = findViewById(R.id.editTextName);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(editTextName.getText().toString());
            }
        });
    }


    private void save(String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        if (firebaseUser != null) {
            firebaseUser.updateProfile(profileUpdates);
            Toast.makeText(ChangeName.this, getApplicationContext().getString(R.string.new_email_success), Toast.LENGTH_LONG).show();
            Intent i = new Intent(ChangeName.this,ManageProfile.class);
            startActivity(i);
            finish();
        }
    }
}
