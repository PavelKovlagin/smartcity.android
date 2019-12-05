package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import ru.smartcity.models.Event;
import ru.smartcity.R;
import ru.smartcity.helpers.JsonHelper;

public class ActShowEvent extends AppCompatActivity {

    private Event event;
    private String adressServer, linkSelectEventFromServer, event_id,  eventDescription, eventStatus, eventDate, emailUser;
    private TextView textEventName, textEventDescription, textEventStatus, textEventDate, textEmailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        adressServer = getString(R.string.adressServer);
        linkSelectEventFromServer = getString(R.string.linkSelectEventFromServer);
        event_id = getIntent().getExtras().getString("event_id");
        textEventName = (TextView) findViewById(R.id.textEventName);
        textEventDescription = (TextView) findViewById(R.id.textEventDescription);
        textEventStatus = (TextView) findViewById(R.id.textEventStatus);
        textEventDate = (TextView) findViewById(R.id.textEventDate);
        textEmailUser = (TextView) findViewById(R.id.textEmailUser);

    }

    @Override
    protected void onResume() {
        super.onResume();
        event = JsonHelper.loadEventJsonFromServer(adressServer, linkSelectEventFromServer, event_id);
        if (event != null) {
            textEventName.setText(event.getEventName());
            textEventDescription.setText(event.getEventDescription());
            textEventStatus.setText(event.getStatusName());
            textEventDate.setText(event.getEvent_date());
            textEmailUser.setText(event.getEmail());
        } else {
            Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
        }
    }
}
