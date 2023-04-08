package com.oscargo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerMyPosition;
    private boolean switch_location_checked = false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                    setMyLocation();
                }else{
                    System.out.println(" NO sw_gps.setOnClickListener isChecked");
                    switch_location_checked = false;
                    setMyLocation();
                }
                editor.putBoolean("switch_location_checked", switch_location_checked);
                editor.commit();
            }
        });
    }

    private void setMyLocation() {
        if(switch_location_checked) {
            LatLng myPosition = new LatLng(-34, 151);
            markerMyPosition = mMap.addMarker(new MarkerOptions().position(myPosition).title("Marker in Sydney"));
            markerMyPosition.setPosition(new LatLng(-15, 36));
            myPosition = new LatLng(-15, 36);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        }else{
            mMap.clear();
            System.out.println("switch_location_checked " + switch_location_checked);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if(switch_location_checked) {
            setMyLocation();
        }
    }
}