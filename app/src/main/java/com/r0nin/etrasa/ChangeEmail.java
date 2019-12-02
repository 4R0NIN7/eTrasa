package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmail extends AppCompatActivity {

    protected EditText editTextOldEmail, editTextNewEmail;
    protected Button buttonSave, buttonBack;
    protected final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    protected final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        buttonSave = findViewById(R.id.buttonSaveChangeEmail);
        buttonBack = findViewById(R.id.buttonBackChangeEmail);
        editTextOldEmail = findViewById(R.id.editTextOldEmail);
        editTextNewEmail = findViewById(R.id.editTextNewEmail);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(editTextOldEmail.getText().toString(), editTextNewEmail.getText().toString());
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ManageProfile.class));
                finish();
            }
        });

        progressDialog = new ProgressDialog(ChangeEmail.this);
        progressDialog.setTitle(R.string.progress_bar);
    }


    private void save(String oldEmail, final String newEmail) {
        if (!oldEmail.equals(newEmail) && oldEmail.equals(user.getEmail())) {
            progressDialog.show();
            user.updateEmail(newEmail.trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeEmail.this, getApplicationContext().getString(R.string.new_email_success), Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                Intent i = new Intent(ChangeEmail.this,LoginActivity.class);
                                database.child("users").child(user.getUid()).child("email").setValue(newEmail);
                                progressDialog.dismiss();
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(ChangeEmail.this, getApplicationContext().getString(R.string.new_email_failed), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(ChangeEmail.this, getApplicationContext().getString(R.string.old_email_mismatch), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
}
