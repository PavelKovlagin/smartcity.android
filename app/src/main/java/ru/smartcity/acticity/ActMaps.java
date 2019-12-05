package ru.smartcity.acticity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import ru.smartcity.database.DbSmartCity;
import ru.smartcity.models.Event;
import ru.smartcity.helpers.JsonHelper;
import ru.smartcity.R;

public class ActMaps extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private ImageButton iButtonAddEvent, iButtonUser, iButtomUpdate;
    private String adressServer, linkSelectEventsFroomServer, lastDateUpdate;
    private double latitude = 0, longitude = 0;
    private SupportMapFragment mapFragment;
    private AlertDialog.Builder ad;
    private ProgressBar progressBar;
    private ArrayList<Event> events;
    private SharedPreferences sp;

    private void saveLastDateUpdate() {
        sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String nowDate = dateFormat.format(new Date());
        e.putString("lastDateUpdate", nowDate);
        e.commit();
        Log.i("SaveLastDateUpdate", nowDate);
    }

    private String loadLastDateUpdate() {
        sp = getPreferences(MODE_PRIVATE);
        String lastDateUpdate = sp.getString("lastDateUpdate", "0000-01-01 00:00:00");
        Log.i("LoadLastDateUpdate", lastDateUpdate);
        return lastDateUpdate;
    }

    private void mapUpdate() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        Log.i("Handler", "progressBarVISIBLE");
                        break;
                    case 1:
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        Log.i("Handler", "progressBarINVISIBLE");
                        break;
                    case 2:
                        Toast.makeText(ActMaps.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                        Log.i("Handler", "BadLoad");
                        break;
                    case 3:
                        mapFragment.getMapAsync(ActMaps.this);
                        saveLastDateUpdate();
                        Log.i("Handler", "updateMap");
                        break;
                }
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                ArrayList<Event> events = JsonHelper.getEventsFromServer(adressServer, linkSelectEventsFroomServer, loadLastDateUpdate());
                if (events == null){
                    handler.sendEmptyMessage(2);
                } else {
                    for (Event event : events) {
                        Log.i("ArrayList", event.toString());
                    }
                    DbSmartCity dbSmartCity = new DbSmartCity(ActMaps.this);
                    dbSmartCity.insertOrUpdateEventsFromArrayList(events);
                    handler.sendEmptyMessage(3);
                }
                handler.sendEmptyMessage(1);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        adressServer = getString(R.string.adressServer);
        linkSelectEventsFroomServer = getString(R.string.linkSelectEventsFromServer);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        iButtomUpdate = (ImageButton) findViewById(R.id.iButtonUpdate);
        iButtomUpdate.setOnClickListener(this);
        iButtonAddEvent = (ImageButton) findViewById(R.id.iButtonAddEvent);
        iButtonAddEvent.setOnClickListener(this);
        iButtonUser = (ImageButton) findViewById(R.id.iButtonUser);
        iButtonUser.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    protected void onResume() {
        super.onResume();
        if (!checkPermissions()) return;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final DbSmartCity dbSmartCity = new DbSmartCity(this);
        events = dbSmartCity.getEventsArrayList();
//        Event someEvent = new Event();
//        someEvent.setLatitude(56.147630);
//        someEvent.setLongitude(40.392106);
//        events.add(someEvent);
        for (Event event:events){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLongitude(), event.getLatitude())));
                    marker.setTag(event.getEvent_id());
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                DbSmartCity dbSmartCity1 = new DbSmartCity(ActMaps.this);
                final Event event = dbSmartCity.getEventFromSQL(marker.getTag().toString());
                Log.i("MarkerEvent", event.toString());
                ad = new AlertDialog.Builder(ActMaps.this);
                ad.setTitle(event.getEventName());  // заголовок
                if (event.getEventDescription().length() > 100) {
                    ad.setMessage(event.getEventDescription().substring(0,100) + "..."); // сообщение
                } else {
                    ad.setMessage(event.getEventDescription()); // сообщение
                }
                ad.setPositiveButton("Открыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent showEvent = new Intent(ActMaps.this, ActShowEvent.class);
                        showEvent.putExtra("event_id", String.valueOf(event.getEvent_id()));
                        startActivity(showEvent);
                    }
                });
                ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ActMaps.this, "Событие закрывается, с этим проблем не возникло)))", Toast.LENGTH_SHORT).show();
                    }
                });
                ad.show();
                return false;
            }
        });

        //mMap.addMarker(new MarkerOptions().position(vladimir).title("Marker in Vladimir"));

        if (checkPermissions()) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iButtonAddEvent:
                Intent addEventAct = new Intent(this, ActAddEvent.class);
                if (!checkPermissions()) {
                    Toast.makeText(this, "Разрешения геолокации запрещены", Toast.LENGTH_SHORT).show();
                } else {
                    if ((latitude > 0) && (longitude > 0)) {
                        addEventAct.putExtra("latitude", latitude);
                        addEventAct.putExtra("longitude", longitude);
                        startActivity(addEventAct);
                    } else {
                        Toast.makeText(this, "Координаты GPS не должны быть равны нулю", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iButtonUpdate:
                mapUpdate();
                break;
        }
    }
}
