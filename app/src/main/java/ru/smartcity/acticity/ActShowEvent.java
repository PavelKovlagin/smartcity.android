package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.smartcity.api.ISmartCityApi;
import ru.smartcity.models.Event;
import ru.smartcity.R;

public class ActShowEvent extends AppCompatActivity {

    private Event event;
    private String adressServer, linkSelectEventFromServer, event_id,  eventDescription, eventStatus, eventDate, emailUser;
    private TextView textEventName, textEventDescription, textEventStatus, textEventDate, textEmailUser;
    private ProgressBar progressBar;
    private ISmartCityApi smartCityApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        event_id = getIntent().getExtras().getString("event_id");
        textEventName = (TextView) findViewById(R.id.textEventName);
        textEventDescription = (TextView) findViewById(R.id.textEventDescription);
        textEventStatus = (TextView) findViewById(R.id.textEventStatus);
        textEventDate = (TextView) findViewById(R.id.textEventDate);
        textEmailUser = (TextView) findViewById(R.id.textEmailUser);

    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        event_id = getIntent().getExtras().getString("event_id");
        Log.i("ActShowEvent. onResume. event_id", event_id);

        smartCityApi.getEvent(event_id).enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                event = response.body().get(0);
                textEventName.setText(event.getEventName());
                textEventDescription.setText(event.getEventDescription());
                textEventStatus.setText(event.getStatusName());
                textEventDate.setText(event.getEvent_date());
                textEmailUser.setText(event.getEmail());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Log.e("ActShowEvent", t.getMessage());
                Toast.makeText(ActShowEvent.this, "false load", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
