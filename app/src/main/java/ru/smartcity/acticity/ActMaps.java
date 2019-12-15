package ru.smartcity.acticity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.smartcity.api.ISmartCityApi;
import ru.smartcity.database.DbSmartCity;
import ru.smartcity.models.Event;

import ru.smartcity.R;

public class ActMaps extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private ImageButton iButtonAddEvent, iButtonUser, iButtomUpdate;
    private double latitude = 0, longitude = 0;
    private SupportMapFragment mapFragment;
    private AlertDialog.Builder ad;
    private ProgressBar progressBar;
    private ArrayList<Event> events;
    private SharedPreferences sPref;
    private ISmartCityApi smartCityApi;

    @SuppressLint("LongLogTag")
    private boolean loadToken() {
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        String access_token = sPref.getString("access_token", "null");
        Log.i("ActMaps. access_token load", access_token);
        if (access_token.equals("null")) {
            return false;
        } else {
            return true;
        }
    }

    private void saveLastDateUpdate() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e = sPref.edit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String nowDate = dateFormat.format(new Date());
        e.putString("lastDateUpdate", nowDate);
        e.commit();
        Log.i("SaveLastDateUpdate", nowDate);
    }

    private String loadLastDateUpdate() {
        sPref = getPreferences(MODE_PRIVATE);
        String lastDateUpdate = sPref.getString("lastDateUpdate", "0000-01-01 00:00:00");
        Log.i("LoadLastDateUpdate", lastDateUpdate);
        return lastDateUpdate;
    }

    private void mapUpdate() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        smartCityApi.getEvents(loadLastDateUpdate()).enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                events = response.body();
                for (Event event : events) {
                    Log.i("ArrayList<Event>", event.toString());
                }
                Toast.makeText(ActMaps.this, "evvents.size=" + events.size(), Toast.LENGTH_SHORT).show();
                DbSmartCity db = new DbSmartCity(ActMaps.this);
                db.insertOrUpdateEventsFromArrayList(events);
                saveLastDateUpdate();
                mapFragment.getMapAsync(ActMaps.this);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                Toast.makeText(ActMaps.this, "Load false", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);

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
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
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
        mMap.clear();
        final DbSmartCity dbSmartCity = new DbSmartCity(this);
        events = dbSmartCity.getEventsArrayList();
//        Event someEvent = new Event();
//        someEvent.setLatitude(56.147630);
//        someEvent.setLongitude(40.392106);
//        events.add(someEvent);
        for (Event event : events) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude())));
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
                    ad.setMessage(event.getEventDescription().substring(0, 100) + "..."); // сообщение
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
            case R.id.iButtonUser:
                if (loadToken()) {
                    //Загрузка профиля пользователя
                    Intent actUser = new Intent(this, ActUser.class);
                    startActivity(actUser);
                    //Toast.makeText(this, "Загрузка профиля", Toast.LENGTH_SHORT).show();
                } else {
                    //Загрузка формы логина
                    Intent actLogin = new Intent(this, ActLogin.class);
                    startActivity(actLogin);
                }
            break;
        }
    }
}
