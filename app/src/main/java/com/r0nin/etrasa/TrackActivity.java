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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends FragmentActivity implements OnMapReadyCallback, CreatePointDialog.CreatePointDialogListener {

    protected GoogleMap mMap;
    protected Circle circle;
    protected Marker marker;





    private String TAG = "TrackActivity";
    private boolean permissionsGranted = false, gpsEnabled = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST = 4321;

    private static final int INTERVAL = 120000;//for every 2 minutes
    private static final int FASTEST_INTERVAL = 1000;  //for every 10 sec if it's sooner

    private static final int SMALLEST_DISPLACEMENT = 100; //for every x meters the locationCallback will go
    private static final float DEFAULT_ZOOM = 15; //Default zoom for camera

    private static double RADIUS = 0;
    private static String POINT_NAME = "";


    private static int  numer = 0;

    private ArrayList<Integer> numerAL = new ArrayList<>();
    private ArrayList<String> titlesAL = new ArrayList<>();
    private ArrayList<String> radiusAL = new ArrayList<>();
    private ArrayList<String> latAL = new ArrayList<>();
    private ArrayList<String> lngAL = new ArrayList<>();


    /*-----------------------------------------------------------*/
    protected ArrayList<String> lat = new ArrayList<>();
    protected ArrayList<String> lng = new ArrayList<>();
    protected ArrayList<String> radiusString = new ArrayList<>();
    protected ArrayList<Integer> numers = new ArrayList<>();
    protected ArrayList<String> title = new ArrayList<>();
    protected ArrayList<String> description = new ArrayList<>();
    protected ArrayList<LatLng> latLngs = new ArrayList<>();
    protected ArrayList<Double> radiusDouble = new ArrayList<>();
    protected ArrayList<Marker> markers = new ArrayList<>();
    protected ArrayList<Circle> circles = new ArrayList<>();
    protected String trackTitle, trackDescription, keyTrack;


    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    protected ImageView settingsDataTrack, endSettingTrack;
    protected boolean changeTrack = false;



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
        endSettingTrack = findViewById(R.id.endSettingTrack);
        endSettingTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreateTrack.class);
                if(!titlesAL.isEmpty()) {
                    intent.putStringArrayListExtra("titles", titlesAL);
                    intent.putStringArrayListExtra("radius", radiusAL);
                    intent.putStringArrayListExtra("latAL", latAL);
                    intent.putStringArrayListExtra("lngAL", lngAL);
                    intent.putIntegerArrayListExtra("numerAL", numerAL);
                    if(changeTrack){
                        intent.putExtra("trackDescription", trackDescription);
                        intent.putExtra("trackTitle", trackTitle);
                        intent.putExtra("keyTrack", keyTrack);
                        intent.putStringArrayListExtra("description",description);
                    }
                    startActivity(intent);
                    finish();
                }else
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.markers_empty), Toast.LENGTH_SHORT).show();
            }
        });

        Intent i = getIntent();
        if(i.hasExtra("lat") || i.hasExtra("lng") || i.hasExtra("radius") || i.hasExtra("description") ||
                i.hasExtra("title") || i.hasExtra("numer") || i.hasExtra("trackDescription") || i.hasExtra("trackTitle")){
            changeTrack = true;
        }



        }

    protected void setMarkersOnMapFromIntent(Intent intent){
        setData(intent);
        for(int i=0;i<latLngs.size();i++){
            setMarkerWithCircleFromIntent(latLngs.get(i),title.get(i),radiusDouble.get(i),numers.get(i));
        }
    }
    private void setMarkerWithCircleFromIntent(LatLng latLng,String title, double radius, int numerS){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(""+radius)
                .visible(true)
                .draggable(true);
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.RED)
                .visible(true);

        marker = mMap.addMarker(markerOptions);
        circle = mMap.addCircle(circleOptions);
        Log.i(TAG,"Options added");
        markers.add(marker);
        circles.add(circle);
        titlesAL.add(title);
        radiusAL.add(String.valueOf(radius));
        latAL.add(String.valueOf(latLng.latitude));
        lngAL.add(String.valueOf(latLng.longitude));
        numerAL.add(numerS);
        numerS++; //zwiekszam numer ten ktory byl podany z intenta
        numer = numerS; //przypisuje numera wartosc do zmiennej globalnej numer
    }

    protected void setData(Intent intent){
        lat = intent.getStringArrayListExtra("lat");
        lng = intent.getStringArrayListExtra("lng");
        radiusString = intent.getStringArrayListExtra("radius");
        description = intent.getStringArrayListExtra("description");
        title = intent.getStringArrayListExtra("title");
        numers = intent.getIntegerArrayListExtra("numer");
        trackTitle = intent.getStringExtra("trackTitle");
        trackDescription = intent.getStringExtra("trackDescription");
        keyTrack = intent.getStringExtra("keyTrack");
        for(int i=0;i<lat.size();i++){
            double lt = Double.valueOf(lat.get(i));
            double lg = Double.valueOf(lng.get(i));
            double rd = Double.valueOf(radiusString.get(i));
            LatLng latLng = new LatLng(lt,lg);
            latLngs.add(latLng);
            radiusDouble.add(rd);
        }
        Log.i(TAG,"setData initialized");
    }

    public void openDialog(){
        CreatePointDialog createPointDialog = new CreatePointDialog();
        createPointDialog.show(getSupportFragmentManager(), "Create Dialog");
    }


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
                    if(RADIUS > 0  && RADIUS != 0 && !TextUtils.isEmpty(POINT_NAME)) {
                        setMarkerWithCircle(latLng);
                        RADIUS = 0;
                        POINT_NAME = "";
                    }else
                        Toast.makeText(TrackActivity.this, TrackActivity.this.getText(R.string.set_data_for_point), Toast.LENGTH_LONG).show();
                }
            });
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    deleteMarker(marker);

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                }
            });
            Intent i = getIntent();
            if(changeTrack){
                try {
                    Log.i(TAG,"try");
                    setMarkersOnMapFromIntent(i);
                }catch (NullPointerException nullEx){
                    nullEx.printStackTrace();
                    Log.i(TAG,"NullPointerException");
                }catch (Exception ex){
                    Log.i(TAG,"Exception");
                }
            }

        }


    }

    private void deleteMarker(Marker marker){
        if(markers.size() > 1) {
            int index = markers.indexOf(marker);
            circles.remove(index);
            markers.remove(marker);
            titlesAL.remove(index);
            radiusAL.remove(index);
            latAL.remove(index);
            lngAL.remove(index);
            mMap.clear();
            for (int i = 0; i < markers.size(); i++) {
                String title = markers.get(i).getTitle();
                LatLng latLng = markers.get(i).getPosition();
                double radius = Double.valueOf(markers.get(i).getSnippet());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet("" + radius)
                        .visible(true)
                        .draggable(true);
                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(radius)
                        .strokeColor(Color.RED)
                        .visible(true);
                mMap.addMarker(markerOptions);
                mMap.addCircle(circleOptions);
                Toast.makeText(TrackActivity.this, TrackActivity.this.getText(R.string.deleted_marker), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            circles.clear();
            markers.clear();
            mMap.clear();
            Toast.makeText(TrackActivity.this, TrackActivity.this.getText(R.string.deleted_marker), Toast.LENGTH_SHORT).show();
        }
    }


    private void setMarkerWithCircle(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(POINT_NAME)
                .snippet(""+RADIUS)
                .visible(true)
                .draggable(true);
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(RADIUS)
                .strokeColor(Color.RED)
                .visible(true);
        marker = mMap.addMarker(markerOptions);
        circle = mMap.addCircle(circleOptions);
        markers.add(marker);
        circles.add(circle);
        titlesAL.add(POINT_NAME);
        radiusAL.add(String.valueOf(RADIUS));
        latAL.add(String.valueOf(latLng.latitude));
        lngAL.add(String.valueOf(latLng.longitude));
        //Toast.makeText(this, String.valueOf(latLng.latitude), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "AL: " + Double.parseDouble(latAL.get(numer)), Toast.LENGTH_SHORT).show();
        numerAL.add(numer);
        numer++;
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
