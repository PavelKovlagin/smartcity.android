package ru.smartcity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.HttpCookie;
import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private TextView textStatusGPS, textLocationGPS, textStatusNet, textLocationNet;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private ImageButton iButtonAddEvent, iButtonUser;
    private Button buttonMapUpdate;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    private double latitude = 0, longitude = 0;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonMapUpdate = (Button) findViewById(R.id.buttonMapUpdate);
        buttonMapUpdate.setOnClickListener(this);
        textStatusGPS = (TextView) findViewById(R.id.textStatusGPS);
        textLocationGPS = (TextView) findViewById(R.id.textLocationGPS);
        textStatusNet = (TextView) findViewById(R.id.textStatusNet);
        textLocationNet = (TextView) findViewById(R.id.textLocationNet);
        iButtonAddEvent = (ImageButton) findViewById(R.id.iButtonAddEvent);
        iButtonAddEvent.setOnClickListener(this);
        iButtonUser = (ImageButton) findViewById(R.id.iButtonUser);
        iButtonUser.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    protected void onResume() {
        super.onResume();
        if (!checkPermissions()) return;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
            checkEnabled();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                textStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                textStatusNet.setText("Status: " + String.valueOf(status));
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (!checkPermissions()) return;
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }
    };

    private void showLocation(Location location) {
        if (location == null) return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            textLocationGPS.setText(formatLocation(location));

        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
            textLocationNet.setText(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null) return "";
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        return String.format("Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT", location.getLatitude(), location.getLongitude(), new Date(location.getTime()));
    }

    private void checkEnabled() {
        textStatusGPS.setText("Status: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        textStatusNet.setText("Status: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<Event> events = new ArrayList<Event>();
        LatLng vladimir = new LatLng(56.147630, 40.392108);
        DbSmartCity dbSmartCity = new DbSmartCity(this);
        events = dbSmartCity.getEventsArrayList();
//        Event someEvent = new Event();
//        someEvent.setLatitude(56.147630);
//        someEvent.setLongitude(40.392106);
//        events.add(someEvent);
        for (Event event:events){
            mMap.addMarker(new MarkerOptions().position(new LatLng(event.getLongitude(), event.getLatitude())).title(event.getEventName()));
        }
        //mMap.addMarker(new MarkerOptions().position(vladimir).title("Marker in Vladimir"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vladimir,14));
        if (!checkPermissions()) return;
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iButtonAddEvent:
                Intent addEventAct = new Intent(this, AddEventAct.class);
                if ((latitude > 0) && (longitude > 0)) {
                    addEventAct.putExtra("latitude", latitude);
                    addEventAct.putExtra("longitude", longitude);
                    startActivity(addEventAct);
                } else {
                    Toast.makeText(this, "Координаты GPS не должны быть равны нулю", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonMapUpdate:
                ArrayList<Event> events = JSONhelper.getEventsFromServer();
                for (Event event:events) {
                    Log.i("ArrayList", event.toString());
                }
                DbSmartCity dbSmartCity = new DbSmartCity(this);
                dbSmartCity.insertOrUpdateEventsFromArrayList(events);
                mapFragment.getMapAsync(this);
                break;
        }
    }
}
