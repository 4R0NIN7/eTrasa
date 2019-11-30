package com.r0nin.etrasa;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends FragmentActivity implements OnMapReadyCallback, CreatePointDialog.CreatePointDialogListener {

    private GoogleMap mMap;
    private Circle circle;
    private Marker marker;
    private ArrayList<Marker> markers;
    private ArrayList<Circle> circles;
    private Point point;
    private String TAG = "MapsActivity";
    private boolean permissionsGranted = false, gpsEnabled = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST = 4321;

    private static final int INTERVAL = 120000;//for every 2 minutes
    private static final int FASTEST_INTERVAL = 1000;  //for every 10 sec if it's sooner

    private static final int SMALLEST_DISPLACEMENT = 100; //for every x meters the locationCallback will go
    private static final float DEFAULT_ZOOM = 15; //Default zoom for camera

    private static final double RADIUS = 200;

    private String pointName, description;
    private int radius, numer = 0;

    private LatLng latLng;

    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ImageView addMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        enableRuntimePermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, this.getText(R.string.gps_enabled), Toast.LENGTH_SHORT).show();
            gpsEnabled = true;
        }else{
            showGPSDisabledAlertToUser();
        }
        markers = new ArrayList<>();
        circles = new ArrayList<>();
        addMarker = findViewById(R.id.addMarker);
    }

    public void openDialog(){
        CreatePointDialog createPointDialog = new CreatePointDialog();
        createPointDialog.show(getSupportFragmentManager(), "Create Dialog");
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(permissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationRequest = new LocationRequest();
            locationRequest.setInterval(INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL); //for every 1 minute if it's sooner
            locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT); //for every 100 meters the locationCallback will go
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    openDialog();
                    if(!TextUtils.isEmpty(pointName) && !TextUtils.isEmpty(description)){
                        numer++;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(pointName)
                                .snippet("Point number: "+ numer + "\n"+"Radius " + radius + "\n"+"Description: +" + description);
                        CircleOptions circleOptions = new CircleOptions()
                                .center(latLng)
                                .radius(RADIUS)
                                .strokeColor(Color.RED)
                                .visible(true);
                        marker = mMap.addMarker(markerOptions);
                        circle = mMap.addCircle(circleOptions);
                        markers.add(marker);
                        circles.add(circle);
                    }

                }
            });
        }
    }

    private void initializeMap() {
        Log.d(TAG,"initializeMap: initialize map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackActivity.this);
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

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                Log.d(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
                currentLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveCamera(latLng, DEFAULT_ZOOM, TrackActivity.this.getText(R.string.current_position).toString());
                //int closest = whichCircleIsClosest(circles);
                //checkingInsideCircle(circles.get(closest),markers.get(closest));
            }
        }
    };


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


    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current longitude and latitude");
        try {
            if (permissionsGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: location was found");
                            currentLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(latLng, DEFAULT_ZOOM,TrackActivity.this.getText(R.string.current_position).toString());
                        } else {
                            Log.d(TAG, "onComplete: location was not found");
                            Toast.makeText(TrackActivity.this, TrackActivity.this.getText(R.string.error_current_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException ex) {
            Log.d(TAG, "getDeviceLocation: SecurityException");
        }
    }


    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

    private LatLng addPoint(LatLng latLng){
        return latLng;
    }


    @Override
    public void applyData(String pointName, int radius, String description) {
        this.pointName = pointName;
        this.radius = radius;
        this.description = description;
    }
}
