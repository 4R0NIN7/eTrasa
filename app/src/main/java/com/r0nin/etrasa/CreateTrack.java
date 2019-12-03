package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTrack extends AppCompatActivity {
    protected Button next, prev,saveDesc, createTrack;
    protected EditText editTextDescriptionCreateTrack;
    protected String TAG = "CreateTrackActivity";

    protected ArrayList<String> titlesAL;
    protected ArrayList<String> radiusAL;
    protected ArrayList<String> descriptionAL;
    protected ArrayList<Integer> numerAL;
    protected ArrayList<String> latAL = new ArrayList<>();
    protected ArrayList<String> lngAL = new ArrayList<>();

    protected TextView textViewPointName;
    protected static int actual = 0;
    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private ProgressDialog progressDialog;
    protected Map<String, Point> points = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_track);
        next = findViewById(R.id.buttonNextCreateTrack);
        prev = findViewById(R.id.buttonPrevCreateTrack);
        textViewPointName = findViewById(R.id.textViewPointName);
        editTextDescriptionCreateTrack = findViewById(R.id.editTextDescriptionCreateTrack);
        saveDesc = findViewById(R.id.buttonSaveDescriptionCreateTrack);
        createTrack = findViewById(R.id.buttonSaveCreateTrack);
        Intent i = getIntent();
        titlesAL = i.getStringArrayListExtra("titles");
        radiusAL = i.getStringArrayListExtra("radius");
        numerAL = i.getIntegerArrayListExtra("numerAL");
        latAL = i.getStringArrayListExtra("latAL");
        lngAL = i.getStringArrayListExtra("lngAL");
        descriptionAL = new ArrayList<>();
        textViewPointName.setText(titlesAL.get(actual));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actual++;
                Log.i(TAG, "Actual " + actual);
                if(actual < titlesAL.size()) {
                    textViewPointName.setText(titlesAL.get(actual));
                    editTextDescriptionCreateTrack.setText(descriptionAL.get(actual));
                    Log.i(TAG, "actual < titlesAL.size() " + actual);
                }else{
                    actual = 0;
                    textViewPointName.setText(titlesAL.get(actual));
                    editTextDescriptionCreateTrack.setText(descriptionAL.get(actual));
                    Log.i(TAG, "actual = 0; " + actual);
                }

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actual--;
                Log.i(TAG, "Actual " + actual);
                if(actual > 0) {
                    textViewPointName.setText(titlesAL.get(actual));
                    editTextDescriptionCreateTrack.setText(descriptionAL.get(actual));
                    Log.i(TAG, "actual > 0 " + actual);
                }else {
                    actual = titlesAL.size() - 1;
                    textViewPointName.setText(titlesAL.get(actual));
                    editTextDescriptionCreateTrack.setText(descriptionAL.get(actual));
                    Log.i(TAG, "actual = titlesAL.size() - 1; " + actual);
                }
            }
        });
        saveDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(editTextDescriptionCreateTrack.getText().toString())) {
                    descriptionAL.add(actual, editTextDescriptionCreateTrack.getText().toString());
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.save_description), Toast.LENGTH_SHORT).show();
                    editTextDescriptionCreateTrack.setText("");
                }
                else
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.description), Toast.LENGTH_SHORT).show();
            }
        });
        createTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < titlesAL.size();i++){
                    String title = titlesAL.get(i), description = descriptionAL.get(i);
                    double radius = Double.parseDouble(radiusAL.get(i));
                    double lat = Double.parseDouble(latAL.get(i));
                    double lng = Double.parseDouble(lngAL.get(i));
                    int numer = numerAL.get(i);
                    Point p = new Point(title,lat,lng,description,firebaseUser.getUid(),numer,radius);
                    Log.i(TAG,p.title);
                    points.put(String.valueOf(numer),p);
                }
                saveToDB();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        //Toast.makeText(getApplicationContext(), titles.get(0) + radius.get(0), Toast.LENGTH_SHORT).show();
        //editTextDescriptionCreateTrack.setText(titles.get(0) + radius.get(0));
        progressDialog = new ProgressDialog(CreateTrack.this);
        progressDialog.setTitle(R.string.progress_bar);
    }

    private void saveToDB(){
        progressDialog.show();
        String KEY = database.child("tracks").push().getKey();
        Track track = new Track(KEY,firebaseUser.getUid(),"Created by " + firebaseUser.getDisplayName() + " has " + points.size() + " points.", points);
        database.child("tracks").child(KEY).setValue(track);
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Added to DB", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Added to DB " + track.keyTrack + " title " + track.title);
    }


}
