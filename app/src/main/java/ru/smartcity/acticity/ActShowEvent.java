package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.smartcity.adapter.DataAdapter;
import ru.smartcity.api.ISmartCityApi;
import ru.smartcity.models.Comment;
import ru.smartcity.models.Event;
import ru.smartcity.R;
import ru.smartcity.models.ServerResponse;

public class ActShowEvent extends AppCompatActivity implements View.OnClickListener {

    private Event event;
    private String event_id;
    private TextView textEventName, textEventDescription, textEventStatus, textEventDate, textEmailUser;
    private EditText editComment;
    private ProgressBar progressBar;
    private ISmartCityApi smartCityApi;
    private ArrayList<Comment> comments;
    private String TAG = this.getClass().toString();
    private RecyclerView recyclerView;
    private SharedPreferences sPref;
    private Button buttonSendComment;

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
        editComment = (EditText) findViewById(R.id.ActShowEvent_editComment);
        buttonSendComment = (Button) findViewById(R.id.ActShowEvent_buttonSendComment);
        buttonSendComment.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.ActShowEvent_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private String loadToken() {
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        String access_token = sPref.getString("access_token", "null");
        return access_token;
    }

    private void sendComment(String comment) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("comment", comment);
        params.put("event_id", event_id);
        smartCityApi.sendComment("Bearer " + loadToken(), params).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {

                    loadComments();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(ActShowEvent.this, getString(R.string.falseServer), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void loadEvent() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        //event_id = getIntent().getExtras().getString("event_id");
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

    private void loadComments() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        smartCityApi.getEventComments(event_id).enqueue(new Callback<ArrayList<Comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                if (response.isSuccessful()) {
                    comments = response.body();
                    DataAdapter adapter = new DataAdapter(ActShowEvent.this, comments);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(ActShowEvent.this, String.valueOf(adapter.getItemCount()), Toast.LENGTH_SHORT).show();
                    for (Comment comment : comments) {
                        Log.i(TAG + " loadComments", comment.toString());
                    }
                } else {
                    Log.e(TAG + " loadComments", response.code() + " " + response.errorBody());
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {
                Log.e(TAG + " loadComments", "false load");
                Toast.makeText(ActShowEvent.this, "false load", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvent();
        loadComments();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ActShowEvent_buttonSendComment:
                sendComment(editComment.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonSendComment.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editComment.setText("");
                break;
        }
    }
}
