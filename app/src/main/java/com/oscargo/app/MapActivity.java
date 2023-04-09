package com.oscargo.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerMyPosition;
    private boolean switch_location_checked = false;
    SharedPreferences sharedPreferences;
    public static final int UPDATE_INTERVAL = 10000;
    public static final int FASTEST_INTERVAL = 5000;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    boolean UpdateTo = false;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        setLocationRequest();
        setLocationCallback();

        sharedPreferences = getSharedPreferences("oscargo_shared_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch_location_checked = sharedPreferences.getBoolean("switch_location_checked", false);

        Switch switch_location = findViewById(R.id.switch_location);
        switch_location.setChecked(switch_location_checked);

        switch_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_location.isChecked()) {
                    System.out.println("sw_gps.setOnClickListener isChecked");
                    switch_location_checked = true;
                    startLocationUpdates();
                }else{
                    System.out.println(" NO sw_gps.setOnClickListener isChecked");
                    switch_location_checked = false;
                    setMyLocation(null);
                }
                editor.putBoolean("switch_location_checked", switch_location_checked);
                editor.commit();
            }
        });
        UpdateGps();
    }

    private void setLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                UpdateUiValues(location);
                sendLocation(location);
                setMyLocation(location);
                super.onLocationResult(locationResult);
            }
        };
    }

    private void setLocationRequest() {
        locationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setMyLocation(Location location) {
        if(switch_location_checked && location!=null) {
            LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
            markerMyPosition = mMap.addMarker(new MarkerOptions().position(myPosition).title("Mi ubicación"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 8.0f ) );
        }else{
            mMap.clear();
            System.out.println("switch_location_checked " + switch_location_checked);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if(switch_location_checked) {
            setMyLocation(null);
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText( this, "Debe dar permisos para obtener su ubicación", Toast.LENGTH_SHORT).show();
            return;
        }else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }
    private void checkPermision() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                System.out.println("Precise location access granted.");
                                UpdateGps();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                System.out.println("Only approximate location access granted.");
                                UpdateGps();
                            } else {
                                // No location access granted.
                                System.out.println("No location access granted.");
                                finish();
                            }
                        }
                );
        // ...
        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void UpdateGps(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) " +
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
            System.out.println("UpdateGps no permision");
            checkPermision();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.d(TAG, "location " + location);
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                UpdateUiValues(location);
                                sendLocation(location);
                            }
                        }
                    });
        }
    }

    private void UpdateUiValues(Location location) {
        //tv_lat.setText(String.valueOf(location.getLatitude()));
        //tv_lon.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("onRequestPermissionsResult requestCode: " + requestCode);
        String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        String accessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        System.out.println("accessFineLocation: " + accessFineLocation);
        System.out.println("accessCoarseLocation: " + accessCoarseLocation);
        switch (requestCode){//2074556243
            case 99:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    UpdateGps();
                }else{
                    Toast.makeText(this, "La app necesita permisos de posición", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void sendLocation(Location location) {

        Log.d(TAG, "getLastLocation location getLongitude: " + location.getLongitude());
        Log.d(TAG, "getLastLocation location getLatitude: " + location.getLatitude());
        lat = location.getLatitude();
        lng = location.getLongitude();
        String name = sharedPreferences.getString("name", null);
        String phone = sharedPreferences.getString("phone", null);
        String patent = sharedPreferences.getString("patent", null);
        int capacity = 0;
        if(sharedPreferences.getInt("capacity", 0)>0) {
            capacity = sharedPreferences.getInt("capacity", 0);
        }
        String destination = sharedPreferences.getString("destination", null);
        String returnValue = sharedPreferences.getString("return", null);

        if(!checkInfo()){
            Toast.makeText(this, "Los datos no están completos, vaya a configuración", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Create a new user with a first and last name
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina"));
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String strDate = formatter.format(date);
        Long dateTime = date.getTime()- 10800 * 1000;
        Timestamp ts=new Timestamp(dateTime);
        strDate = formatter.format(ts);

        Map<String, Object> position = new HashMap<>();
        position.put("capacity", capacity);
        position.put("color", "green");
        position.put("icon", "truck");
        position.put("datetime", strDate);
        position.put("lat", lat); //-38.75421086939975
        position.put("lng", lng); //-62.26506815533449
        position.put("loaded", 0);
        position.put("name", name);
        position.put("patent", patent);
        position.put("phone", phone);
        position.put("destination", destination);
        position.put("return", returnValue);

        // Add a new document with a generated ID
        db.collection("positions")
                .document(patent)
                //.collection("posic")
                .set(position)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void documentReference) {
                        Log.d(TAG, "################## DocumentSnapshot added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "################ Error adding document", e);
                    }
                });
    }

    private boolean checkInfo() {
        String name = sharedPreferences.getString("name", null);
        String phone = sharedPreferences.getString("phone", null);
        String patent = sharedPreferences.getString("patent", null);
        int capacity = 0;
        if(sharedPreferences.getInt("capacity", 0)>0) {
            capacity = sharedPreferences.getInt("capacity", 0);
        }
        String destination = sharedPreferences.getString("destination", null);
        String returnValue = sharedPreferences.getString("return", null);
        if(name==null){
            return false;
        }
        if(patent==null){
            return false;
        }
        if(phone==null){
            return false;
        }
        return true;
    }
}