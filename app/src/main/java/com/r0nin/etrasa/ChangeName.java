package com.r0nin.etrasa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeName extends AppCompatActivity {

    protected EditText editTextName;
    protected Button buttonSave, buttonBack;
    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    protected final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        buttonSave = findViewById(R.id.buttonSaveChangeName);
        buttonBack = findViewById(R.id.buttonBackChangeName);
        editTextName = findViewById(R.id.editTextName);
        Bundle bundle = getIntent().getExtras();


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(editTextName.getText().toString());
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ManageProfile.class));
                finish();
            }
        });

        if(bundle != null){
            buttonBack.setVisibility(View.GONE);
        }
        else {
            buttonBack.setVisibility(View.VISIBLE);
        }

        progressDialog = new ProgressDialog(ChangeName.this);
        progressDialog.setTitle(R.string.progress_bar);
    }


    private void save(String name) {
        progressDialog.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        if (firebaseUser != null) {
            firebaseUser.updateProfile(profileUpdates);
            database.child("users").child(firebaseUser.getUid()).child("displayName").setValue(name);
            Toast.makeText(ChangeName.this, getApplicationContext().getString(R.string.new_name_success), Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent i = new Intent(ChangeName.this,LoginActivity.class);
            progressDialog.dismiss();
            startActivity(i);
            finish();
        }
    }
}
