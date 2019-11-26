package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    protected Button buttonCreateTrack, buttonTrack, buttonChangeProfile, buttonSignOut;
    protected TextView textViewHello;
    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        textViewHello = findViewById(R.id.textViewHello);
        buttonChangeProfile = findViewById(R.id.buttonChangeProfile);
        buttonTrack = findViewById(R.id.buttonTrack);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonCreateTrack = findViewById(R.id.buttonCreateTrack);
        String name = firebaseUser.getDisplayName();
        if(!(name == null)) {
            String hello = this.getString(R.string.hello) + name + " !";
            textViewHello.setText(hello);
        }else
            textViewHello.setText(this.getString(R.string.change_your_name));
    }
}
