package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    protected SharedPreferences sharedpreferences;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    protected DatabaseReference dbTracks = FirebaseDatabase.getInstance().getReference("/tracks");
    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "MainActivity";
    private IntentFilter filterLowBattery = new IntentFilter(Intent.ACTION_BATTERY_LOW);
    private IntentFilter filterBatteryStatusOk = new IntentFilter(Intent.ACTION_BATTERY_OKAY);
    private IntentFilter filterPowerConnected = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
    private IntentFilter filterPowerDisconnected = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
    private RecyclerView recyclerView;
    private TrackAdapter mAdapter;
    protected ArrayList<Track> tracks = new ArrayList<>();
    protected ArrayList<String> keys = new ArrayList<>();
    protected RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycleView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        try {
            dbTracks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Track track = dataSnapshot1.getValue(Track.class);
                        tracks.add(0,track);
                        keys.add(track.getKeyTrack());
                        //Toast.makeText(getApplicationContext(), "onDataChange", Toast.LENGTH_SHORT).show();
                        HashSet<Track> hashSet = new HashSet<Track>();
                        hashSet.addAll(tracks);
                        tracks.clear();
                        tracks.addAll(0,hashSet);
                        Log.i(TAG, "onDataChange");
                    }
                    mAdapter = new TrackAdapter(keys, tracks, MainActivity.this);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onCancelled");
                }
            });
        }catch(NullPointerException nullEx){
            Toast.makeText(this, "Null pointer", Toast.LENGTH_SHORT).show();
            nullEx.printStackTrace();
        }catch (Exception ex){
            Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        sharedpreferences = getSharedPreferences(LoginActivity.STORE_LOG,
                Context.MODE_PRIVATE);


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(R.string.progress_bar);

        database = FirebaseDatabase.getInstance().getReference();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        User user = new User(firebaseUser.getEmail(),firebaseUser.getUid(),firebaseUser.getDisplayName(),token);
                        database.child("users").child(user.uid).setValue(user);
                        Log.d(TAG, "Token "+token);
                    }
                });
        database.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                Log.i(TAG, "Value is: " + value);
                Toast.makeText(getApplicationContext(), "Welcome " + value.displayName + "!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.createTrack:
            Intent intentCreateTrack = new Intent(getApplicationContext(),TrackActivity.class);
            startActivity(intentCreateTrack);
            finish();
            return(true);
        case R.id.profile:
            Intent intentProfile = new Intent(getApplicationContext(),ManageProfile.class);
            startActivity(intentProfile);
            finish();
            return(true);
        case R.id.settings:
            Intent intentSettings = new Intent(getApplicationContext(),Settings.class);
            startActivity(intentSettings);
            finish();
            return(true);
        case R.id.about:
            Toast.makeText(this, R.string.about_toast, Toast.LENGTH_LONG).show();
            return(true);
        case R.id.signOut:
            progressDialog.show();
            mAuth.signOut();
            Intent intentSignOff = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intentSignOff);
            progressDialog.dismiss();
            finish();
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }


    private void showNotification(String title, String message, String ChannelID, String ChannelName) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ChannelID,
                    ChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ChannelID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        mNotificationManager.notify(0, builder.build());
    }


    private BroadcastReceiver broadcastReceiverLowBattery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean stateShowNotifications = sharedpreferences.getBoolean("state_show_notifications",false);
            if(stateShowNotifications)
                showNotification(getApplicationContext().getString(R.string.notification_battery_low),getApplicationContext().getString(R.string.battery_low),"BatteryID", "BatteryName");
            else
                Toast.makeText(context, getApplicationContext().getString(R.string.notification_battery_low), Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver broadcastReceiverBatteryOk = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean stateShowNotifications = sharedpreferences.getBoolean("state_show_notifications",false);
            if(stateShowNotifications)
                showNotification(getApplicationContext().getString(R.string.notification_battery_ok),getApplicationContext().getString(R.string.battery_ok),"BatteryID","BatteryName");
            else
                Toast.makeText(context, getApplicationContext().getString(R.string.notification_battery_ok), Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver broadcastReceiverPowerConnected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean stateShowNotifications = sharedpreferences.getBoolean("state_show_notifications",false);
            if(stateShowNotifications)
                showNotification(getApplicationContext().getString(R.string.notification_power_con),getApplicationContext().getString(R.string.power_con),"PowerID", "PowerName");
            else
                Toast.makeText(context, getApplicationContext().getString(R.string.notification_power_con), Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver broadcastReceiverPowerDisonnected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean stateShowNotifications = sharedpreferences.getBoolean("state_show_notifications",false);
            if(stateShowNotifications)
                showNotification(getApplicationContext().getString(R.string.notification_power_discon),getApplicationContext().getString(R.string.notification_power_discon),"PowerID","PowerName");
            else
                Toast.makeText(context, getApplicationContext().getString(R.string.notification_power_discon), Toast.LENGTH_LONG).show();

        }
    };


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiverLowBattery, filterLowBattery);
        registerReceiver(broadcastReceiverBatteryOk, filterBatteryStatusOk);
        registerReceiver(broadcastReceiverPowerConnected, filterPowerConnected);
        registerReceiver(broadcastReceiverPowerDisonnected, filterPowerDisconnected);
        Log.i(TAG,"onResumeNotifications");

    }

    @Override
    public void onPause() {
        unregisterReceiver(broadcastReceiverLowBattery);
        unregisterReceiver(broadcastReceiverBatteryOk);
        unregisterReceiver(broadcastReceiverPowerConnected);
        unregisterReceiver(broadcastReceiverPowerDisonnected);
        Log.i(TAG,"onPauseNotifications");
        super.onPause();

    }

}
