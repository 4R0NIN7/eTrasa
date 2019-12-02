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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends FragmentActivity implements OnMapReadyCallback, CreatePointDialog.CreatePointDialogListener {

    private GoogleMap mMap;
    private Circle circle;
    private Marker marker;
    private ArrayList<Marker> markers;
    private ArrayList<Circle> circles;

    private String TAG = "MapsActivity";
    private boolean permissionsGranted = false, gpsEnabled = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST = 4321;

    private static final int INTERVAL = 120000;//for every 2 minutes
    private static final int FASTEST_INTERVAL = 1000;  //for every 10 sec if it's sooner

    private static final int SMALLEST_DISPLACEMENT = 100; //for every x meters the locationCallback will go
    private static final float DEFAULT_ZOOM = 15; //Default zoom for camera

    private static double RADIUS = 20;
    private static String POINT_NAME = "";


    private static int  numer = 0;

    private LatLng latLng;

    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    protected ImageView settingsDataTrack;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        enableRuntimePermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, this.getText(R.string.gps_enabled), Toast.LENGTH_SHORT).show();
            gpsEnabled = true;
        }else{
            showGPSDisabledAlertToUser();
        }
        settingsDataTrack = findViewById(R.id.settingsDataTrack);
        settingsDataTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        markers = new ArrayList<>();
        circles = new ArrayList<>();

    }

    public void openDialog(){
        CreatePointDialog createPointDialog = new CreatePointDialog();
        createPointDialog.show(getSupportFragmentManager(), "Create Dialog");
    }
    /*
    private void autocompleteFind(String query){
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setCountry("au")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            }
        });

    }
    */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (permissionsGranted) {
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
                    if(RADIUS != 0 && !TextUtils.isEmpty(POINT_NAME))
                        setMarkerWithCircle(latLng);
                    else
                        Toast.makeText(TrackActivity.this, TrackActivity.this.getText(R.string.set_data_for_point), Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    private void setMarkerWithCircle(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(POINT_NAME)
                .snippet(numer + "\n"+RADIUS + "\n"+latLng.latitude+"\n"+latLng.longitude+"\n")
                .visible(true);
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



    private void initializeMap() {
        Log.d(TAG,"initializeMap: initialize map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTrack);
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

    @Override
    public void applyData(String pointName, int radius) {
        POINT_NAME = pointName;
        RADIUS = (double) radius;
    }
}
