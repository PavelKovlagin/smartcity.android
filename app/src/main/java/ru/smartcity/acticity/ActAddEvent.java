package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.smartcity.R;
import ru.smartcity.api.ISmartCityApi;
import ru.smartcity.models.ServerResponse;

public class ActAddEvent extends AppCompatActivity implements View.OnClickListener {

    private double latitude = 0, longitude = 0;
    private TextView textLongitude, textLatitude;
    private EditText editEventName, editEventDescription;
    private Button buttonAddEvent;
    private ISmartCityApi smartCityApi;
    private ProgressBar progressBar;
    private SharedPreferences sPref;

    private String loadToken() {
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        return sPref.getString("access_token", "null");
    }

    private void addEvent(String token) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("eventName", editEventName.getText().toString());
        params.put("eventDescription", editEventDescription.getText().toString());
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        smartCityApi.addEvent("Bearer " + token, params).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("ActAddEvent. Response", response.body().toString());
                } else {
                    Log.i("ActAddEvent. Response", response.code() + " " +response.errorBody());
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("ActAddEvent. Response", "false");
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);

        latitude = getIntent().getExtras().getDouble("latitude");
        longitude = getIntent().getExtras().getDouble("longitude");

        buttonAddEvent = (Button) findViewById(R.id.buttonAddEvent);
        buttonAddEvent.setOnClickListener(this);
        editEventName = (EditText)findViewById(R.id.editEventName);
        editEventDescription = (EditText) findViewById(R.id.editEventDescription);
        textLatitude = (TextView) findViewById(R.id.textLatitude);
        textLongitude = (TextView) findViewById(R.id.textLongitude);
        textLongitude.setText("Долгота: " + String.valueOf(longitude));
        textLatitude.setText("Широта :" + String.valueOf(latitude));
        progressBar = (ProgressBar) findViewById(R.id.ActAddEvent_progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddEvent:
                String token = loadToken();
                if (loadToken().equals("null")) {
                    Intent actLogin = new Intent(this, ActLogin.class);
                    startActivity(actLogin);
                } else {
                    addEvent(token);
                }
                break;
        }
    }
}
