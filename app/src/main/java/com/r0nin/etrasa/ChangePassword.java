package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    protected EditText editTextOldPassword, editTextNewPassword;
    protected Button buttonSave, buttonBack;
    protected final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    protected final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        buttonSave = findViewById(R.id.buttonSaveChangePassword);
        buttonBack = findViewById(R.id.buttonBackChangePassword);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(editTextOldPassword.getText().toString(),editTextNewPassword.getText().toString());
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ManageProfile.class));
                finish();
            }
        });

        progressDialog = new ProgressDialog(ChangePassword.this);
        progressDialog.setTitle(R.string.progress_bar);
    }


    private void save(String oldPassword, String newPassword) {
        if (!oldPassword.equals(newPassword) && validateData(newPassword)) {
            progressDialog.show();
            user.updatePassword(newPassword.trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePassword.this, getApplicationContext().getString(R.string.password_updated), Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                Intent i = new Intent(ChangePassword.this,LoginActivity.class);
                                progressDialog.dismiss();
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(ChangePassword.this, getApplicationContext().getString(R.string.password_updated_failed), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(ChangePassword.this, getApplicationContext().getString(R.string.password_mismatch), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }


    private boolean validateData(String password){
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), this.getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length() < 8){
            Toast.makeText(getApplicationContext(), this.getString(R.string.password_length), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
