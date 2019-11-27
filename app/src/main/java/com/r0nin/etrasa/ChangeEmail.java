package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ChangeEmail extends AppCompatActivity {

    protected EditText editTextOldEmail, editTextNewEmail;
    protected Button buttonSave;
    protected final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        buttonSave = findViewById(R.id.buttonSave);
        editTextOldEmail = findViewById(R.id.editTextOldEmail);
        editTextNewEmail = findViewById(R.id.editTextNewEmail);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(editTextOldEmail.getText().toString(), editTextNewEmail.getText().toString());
            }
        });
    }


    private void save(String oldEmail, String newEmail) {
        if (!oldEmail.equals(newEmail) && oldEmail.equals(user.getEmail())) {
            user.updateEmail(newEmail.trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeEmail.this, getApplicationContext().getString(R.string.new_email_success), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(ChangeEmail.this,ManageProfile.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(ChangeEmail.this, getApplicationContext().getString(R.string.new_email_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(ChangeEmail.this, getApplicationContext().getString(R.string.old_email_mismatch), Toast.LENGTH_LONG).show();
        }
    }
}
