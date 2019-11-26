package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


public class CheckProfile extends AppCompatActivity {

    protected Button buttonImage, buttonEmail, buttonPassword, buttonSave;
    protected EditText editTextName, editTextEmail, editTextPassword;
    protected ImageView imageViewImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        buttonEmail = findViewById(R.id.buttonEmail);
        buttonImage = findViewById(R.id.buttonImage);
        buttonPassword = findViewById(R.id.buttonPassword);
        buttonSave = findViewById(R.id.buttonSave);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        imageViewImage = findViewById(R.id.imageViewImage);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        editTextName.setText(firebaseUser.getDisplayName());
        editTextEmail.setText(firebaseUser.getEmail());
    }


    private void updateName(FirebaseUser firebaseUser){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(editTextName.getText().toString()).build();
        firebaseUser.updateProfile(profileUpdates);
    }

    private void updateEmail(FirebaseUser firebaseUser){
        firebaseUser.updateEmail(editTextEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckProfile.this, getApplicationContext().getString(R.string.email_updated), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CheckProfile.this, getApplicationContext().getString(R.string.email_updated_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser firebaseUser){
        firebaseUser.updatePassword(editTextPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckProfile.this, getApplicationContext().getString(R.string.password_updated), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CheckProfile.this, getApplicationContext().getString(R.string.password_updated_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateImage(){

    }


}
