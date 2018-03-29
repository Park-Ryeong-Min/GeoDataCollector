package com.candypoint.neo.geodatacollector.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.candypoint.neo.geodatacollector.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by myown on 2018. 3. 28..
 */

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_view_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng seoul = new LatLng(37.52487, 126.92723);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions
                .position(seoul)
                .title("서울 여의도");

        this.googleMap.addMarker(markerOptions);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
    }
}
