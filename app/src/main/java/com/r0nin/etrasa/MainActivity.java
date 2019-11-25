package com.r0nin.etrasa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final FirebaseUser user = null;
    private FirebaseAuth mAuth;

    protected TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        textView1 = findViewById(R.id.textView1);
        assert user != null;
        textView1.setText(user.getEmail() + "ver: " + user.isEmailVerified());
    }
}
