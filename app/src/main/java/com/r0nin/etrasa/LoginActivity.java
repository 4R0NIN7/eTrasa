package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    protected EditText editTextPassword, editTextEmail;
    protected Button buttonSignIn, buttonCreateAcc, buttonForgotPassword;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkConnection();

        editTextEmail =  findViewById(R.id.textViewEmailDisplay);
        editTextPassword =  findViewById(R.id.editTextPassword);
        buttonSignIn =  findViewById(R.id.buttonSignIn);
        buttonCreateAcc =  findViewById(R.id.buttonCreateAcc);
        buttonForgotPassword =  findViewById(R.id.buttonForgotPassword);
        mAuth = FirebaseAuth.getInstance();
        db  = FirebaseFirestore.getInstance();
        buttonCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(editTextEmail.getText().toString(),editTextPassword.getText().toString());
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(editTextEmail.getText().toString(),editTextPassword.getText().toString());
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(editTextEmail.getText().toString(),editTextPassword.getText().toString());
            }
        });





    }



    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void checkConnection(){
        if(isOnline()){
        }else{
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.connect_internet) , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    //tworzenie nowego konta
    private void createAccount(String email, String password) {
        Log.e(TAG, "M_createAccount: " + email);
        if (!validateData(email, password)) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "M_createAccount: Success!");
                            sendEmailVerification();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "M_createAccount: Fail!", task.getException());
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.cannot_create_acc) , Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    //logowanie użytkownika
    private void signIn(String email, String password){
        Log.e(TAG, "M_signIn " + email);
        if(!validateData(email,password))
            return;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "M_signIn: Success!");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "M_signIn: Fail!", task.getException());
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.check_data) , Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.auth_failed) , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.send_email_verf) + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification failed!", task.getException());
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.send_email_verf_failed),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    //walidacja danych
    private boolean validateData(String email, String password){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), this.getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), this.getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length() < 8){
            Toast.makeText(getApplicationContext(), this.getString(R.string.password_length), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



}
