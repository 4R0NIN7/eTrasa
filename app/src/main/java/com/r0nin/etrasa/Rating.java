package com.r0nin.etrasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Rating extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button buttonSubmit;
    private float rat;
    private float sumOfRates;
    private int howMuchPeople;
    private String trackId;
    private Map<String, Float> usersWhichHaveRated = new HashMap<>();

    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    protected FirebaseDatabase db = FirebaseDatabase.getInstance();
    protected DatabaseReference usersRef = db.getReference("tracks");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ratingBar = findViewById(R.id.ratingBar);
        buttonSubmit = findViewById(R.id.buttonRateSave);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rat = rating;
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDb(rat,sumOfRates,howMuchPeople);
                Intent i = new Intent(Rating.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        Intent intent = getIntent();
        if(intent.hasExtra("sumOfRates") && intent.hasExtra("howMuchPeople")&& intent.hasExtra("trackId")){
            sumOfRates = intent.getFloatExtra("sumOfRates",0);
            howMuchPeople = intent.getIntExtra("howMuchPeople",0);
            trackId = intent.getStringExtra("trackId");
        }

    }
    private void saveToDb(final float rat, final float sumOfRates, final int howMuchPeople) {
        if (firebaseUser != null) {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Track track = ds.getValue(Track.class);
                        if (track.getKeyTrack().equals(trackId)) {
                            float s1 = sumOfRates+rat;
                            float s2 = howMuchPeople+1;
                            final float score = s1/s2;
                            usersWhichHaveRated = track.getUsersWhichHaveRated();
                            if(usersWhichHaveRated != null) {
                                usersWhichHaveRated.put(firebaseUser.getUid(), rat);
                                usersRef.child(track.getKeyTrack()).child("rating").setValue(score);
                                usersRef.child(track.getKeyTrack()).child("sumOfRates").setValue(sumOfRates + rat);
                                usersRef.child(track.getKeyTrack()).child("howMuchPeople").setValue(howMuchPeople + 1);
                                usersRef.child(track.getKeyTrack()).child("usersWhichHaveRated").setValue(usersWhichHaveRated);
                            }else
                            {
                                usersWhichHaveRated = new HashMap<>();
                                usersWhichHaveRated.put(firebaseUser.getUid(), rat);
                                usersRef.child(track.getKeyTrack()).child("rating").setValue(score);
                                usersRef.child(track.getKeyTrack()).child("sumOfRates").setValue(sumOfRates + rat);
                                usersRef.child(track.getKeyTrack()).child("howMuchPeople").setValue(howMuchPeople + 1);
                                usersRef.child(track.getKeyTrack()).child("usersWhichHaveRated").setValue(usersWhichHaveRated);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            usersRef.addListenerForSingleValueEvent(valueEventListener);
            Toast.makeText(Rating.this, getApplicationContext().getString(R.string.new_rate_success), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Rating.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
