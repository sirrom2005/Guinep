package com.trafalgartmc.guinep;
/*
 * Created by Rohan Morris
 * on 6/19/2017.
 */

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.trafalgartmc.guinep.Adapters.ItineraryDetailAdapter;

public class TravelMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private double[] mapCoords;
    private String[] info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_map);

        mapCoords   = getIntent().getDoubleArrayExtra(ItineraryDetailAdapter.MAP_COORDS);
        info        = getIntent().getStringArrayExtra(ItineraryDetailAdapter.MAP_INFO);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng departure= new LatLng(mapCoords[0],mapCoords[1]);
        LatLng arrival  = new LatLng(mapCoords[2],mapCoords[3]);
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        }*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(departure,5.0f));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.addMarker(
                new MarkerOptions()
                        .title(info[1])
                        .snippet(info[0])
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_start))
                        .position(departure)
        );

        googleMap.addMarker(
                new MarkerOptions()
                        .title(info[3])
                        .snippet(info[2])
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_end))
                        .position(arrival)
        );

        googleMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(departure)
                .add(arrival)
                .color(Color.WHITE)
                .width(1.0f)
        );

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(departure)
                .zoom(5.0f)
                .bearing(0)
                .build();

        //Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}