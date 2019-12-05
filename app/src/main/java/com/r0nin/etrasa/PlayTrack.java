package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PlayTrack extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastKnownLocation, currentLocation;
    private Circle circle;
    private Marker marker;
    private ArrayList<Marker> markers;
    private ArrayList<Circle> circles;
    private String TAG = "PlayTrack";
    private boolean permissionsGranted = false, gpsEnabled = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST = 4321;

    private static final float DEFAULT_ZOOM = 15; //Default zoom for camera
    private IntentFilter filterPowerDisconnected = new IntentFilter("GPSLocationUpdates");
    private LocationManager locationManager;
    public PlayTrack() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_track);
        enableRuntimePermission();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, filterPowerDisconnected);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, this.getText(R.string.gps_enabled), Toast.LENGTH_SHORT).show();
            gpsEnabled = true;
        }else{
            showGPSDisabledAlertToUser();
        }
        markers = new ArrayList<>();
        circles = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(permissionsGranted) {
            startLocationService();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                PlayTrack.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(LocationService.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Bundle b = intent.getBundleExtra("Location");
            lastKnownLocation = (Location) b.getParcelable("Location");
            if (lastKnownLocation != null) {
                //TU DODAJ AKTUALIZACJE POZYCJI NA MAPIE
                Log.d(TAG, "Location: " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
                currentLocation = lastKnownLocation;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                moveCamera(latLng, DEFAULT_ZOOM, PlayTrack.this.getText(R.string.current_position).toString());
                Toast.makeText(getApplicationContext(),""+latLng.latitude + " " + latLng.longitude,Toast.LENGTH_LONG).show();
                //int closest = whichCircleIsClosest(circles);
                //checkingInsideCircle(circles.get(closest),markers.get(closest));
            }
        }
    };

    private void initializeMap() {
        Log.d(TAG,"initializeMap: initialize map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPlayTrack);
        mapFragment.getMapAsync(PlayTrack.this);
    }

    private void enableRuntimePermission() {
        Log.d(TAG,"enableRuntimePermission: enable permissions");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
                initializeMap();
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{COARSE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
            Log.d(TAG,"enableRuntimePermission: enable permissions by requestPermissions");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    permissionsGranted = true;
                    initializeMap();
                }
            }
        }
    }

    private void showGPSDisabledAlertToUser(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(this.getText(R.string.gps_disabled))
                .setTitle("GPS")
                .setCancelable(false)
                .setPositiveButton(this.getText(R.string.try_to_enable_gps),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(this.getText(R.string.cancel),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private int whichCircleIsClosest(ArrayList<Circle> circles){
        float[] distanceOneCircle = new float[1];
        ArrayList<Float> floats = new ArrayList<>();

        //Pętla wyliczająca dystans do każdego okręgu
        for (Circle c: circles) {
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), c.getCenter().latitude, c.getCenter().longitude, distanceOneCircle);
            floats.add(distanceOneCircle[0]);
        }
        float currentValue = floats.get(0);
        int  smallestIndex = 0;
        //Pętla po tablicy wyliczonych dystansów, gdzie znajduje index najmniejszego elementu - okrąg najbliższy mnie
        for(int i=0; i<floats.size();i++){
            if(floats.get(i) < currentValue){
                currentValue = floats.get(i);
                smallestIndex = i;

            }
        }
        Log.d(TAG,"Min index " + smallestIndex);
        return smallestIndex;
    }

    private void checkingInsideCircle(Circle circle, Marker marker){
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), circle.getCenter().latitude, circle.getCenter().longitude, distance);
        if(distance[0] > circle.getRadius()){
            Toast.makeText(this, this.getText(R.string.not_in_circle) + " " +  marker.getTitle(), Toast.LENGTH_SHORT).show();
        }else {
            InformationDialog informationDialog = new InformationDialog(PlayTrack.this, marker.getSnippet(),marker.getTitle());
            informationDialog.show();
        }
    }



    @Override
    public void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
        unregisterReceiver(mMessageReceiver);
    }


    @Override
    public void onPause() {
        Log.i(TAG,"onPause");
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }




}
