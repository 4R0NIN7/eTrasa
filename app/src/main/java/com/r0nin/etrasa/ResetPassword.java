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

public class ResetPassword extends AppCompatActivity {

    protected EditText editTextResetPasswordEmail;
    protected Button buttonResetPassword;
    protected Button buttonBack;

    protected ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        editTextResetPasswordEmail = findViewById(R.id.editTextResetPasswordEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        buttonBack = findViewById(R.id.buttonBackResetPassword);
        mAuth = FirebaseAuth.getInstance();

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordResetEmail(editTextResetPasswordEmail.getText().toString().trim());
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });


        progressDialog = new ProgressDialog(ResetPassword.this);
        progressDialog.setTitle(R.string.progress_bar);

    }

    private void sendPasswordResetEmail(String email){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.enter_email),Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this, getApplicationContext().getString(R.string.reset_password_success), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ResetPassword.this, getApplicationContext().getString(R.string.reset_password_failed), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        progressDialog.dismiss();
                    }
                });


    }
}
