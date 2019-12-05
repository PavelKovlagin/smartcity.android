package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.smartcity.R;

public class ActAddEvent extends AppCompatActivity implements View.OnClickListener {

    private double latitude = 0, longitude = 0;
    private TextView textLongitude, textLatitude;
    private EditText editEventName, editEventDescription;
    private Button buttonAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddEvent:
//                Event event = new Event(editEventName.getText().toString(), editEventDescription.getText().toString(), latitude, longitude, 1, "Выполняется", 1, "email@mail.ru");
//                DbSmartCity dbSmartCity = new DbSmartCity(this);
//                SQLiteDatabase db = dbSmartCity.getWritableDatabase();
//                dbSmartCity.insertOrUpdateEventIntoSQLite(db, event, true);
//                Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
