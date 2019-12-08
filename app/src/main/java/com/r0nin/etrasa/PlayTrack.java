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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.r0nin.etrasa.LoginActivity.STORE_LOG;

public class PlayTrack extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastKnownLocation, currentLocation;
    private Circle circle;
    private Marker marker;


    private String TAG = "PlayTrack";
    private boolean permissionsGranted = false, gpsEnabled = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST = 4321;

    protected Vibrator vibrator;

    private static final float DEFAULT_ZOOM = 10; //Default zoom for camera
    private IntentFilter filterLocation = new IntentFilter("GPSLocationUpdates");
    private LocationManager locationManager;
    public PlayTrack() {
    }

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
    protected boolean playTrackReady = true;
    protected ImageView imageViewGoBack;
    protected Intent serviceIntent;
    private InformationDialog informationDialog;
    private SharedPreferences sharedpreferences;
    private boolean debug_mode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_track);
        enableRuntimePermission();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, filterLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, this.getText(R.string.gps_enabled), Toast.LENGTH_SHORT).show();
            gpsEnabled = true;
        }else{
            showGPSDisabledAlertToUser();
        }
        Intent i = getIntent();
        if(i.hasExtra("lat") || i.hasExtra("lng") || i.hasExtra("radius") || i.hasExtra("description") ||
                i.hasExtra("title") || i.hasExtra("numer") || i.hasExtra("trackDescription") || i.hasExtra("trackTitle")){
            playTrackReady = true;
        }
        serviceIntent = new Intent(this, LocationService.class);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        imageViewGoBack = findViewById(R.id.imageViewGoBack);
        sharedpreferences = getSharedPreferences(STORE_LOG,
                Context.MODE_PRIVATE);
        boolean debug_mode = sharedpreferences.getBoolean("debug_mode",false);
        imageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                stopService(serviceIntent);
                unregisterReceiver(mMessageReceiver);
                finish();

            }
        });


    }

    protected void setMarkersOnMapFromIntent(Intent intent){
        setData(intent);
        for(int i=0;i<latLngs.size();i++){
            setMarkerWithCircleFromIntent(latLngs.get(i),title.get(i),radiusDouble.get(i),description.get(i));
        }
    }
    private void setMarkerWithCircleFromIntent(LatLng latLng,String title, double radius,String description){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(description)
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
        Intent i = getIntent();
        if(playTrackReady){
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

    private void startLocationService(){
        if(!isLocationServiceRunning()){
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
                Log.d(TAG, "Location: " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
                currentLocation = lastKnownLocation;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                //moveCamera(latLng, DEFAULT_ZOOM, PlayTrack.this.getText(R.string.current_position).toString());
                //Toast.makeText(getApplicationContext(),""+latLng.latitude + " " + latLng.longitude,Toast.LENGTH_SHORT).show();
                int closest = whichCircleIsClosest(circles);
                Circle c = circles.get(closest);
                Marker m = markers.get(closest);
                if(checkWhichCircleIsGone(c,m))
                    checkingInsideCircle(c,m);
                else {
                    if(isLocationServiceRunning()) {
                        if(debug_mode) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.move_next), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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

    private boolean checkWhichCircleIsGone(Circle c,Marker m){
        if(c.getStrokeColor() == Color.GREEN && !m.isVisible()){
            return false;
        }
        else
            return true;
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
        informationDialog = new InformationDialog(PlayTrack.this, marker.getSnippet(),marker.getTitle());
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), circle.getCenter().latitude, circle.getCenter().longitude, distance);
        if(distance[0] > circle.getRadius()){
            if(isLocationServiceRunning()) {
                if(debug_mode)
                    Toast.makeText(this, this.getText(R.string.not_in_circle) + " " + marker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } else {
                //deprecated in API 26
                vibrator.vibrate(500);
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            }
            informationDialog.show();
            circle.setStrokeColor(Color.GREEN);
            circle.setVisible(true);
            marker.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mMessageReceiver, filterLocation);
    }

    @Override
    public void onPause() {
        //stopService(serviceIntent);
        //unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        //stopService(serviceIntent);
        //unregisterReceiver(mMessageReceiver);
        super.onStop();
    }


}
