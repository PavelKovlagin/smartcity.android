package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.smartcity.R;
import ru.smartcity.api.ISmartCityApi;
import ru.smartcity.models.User;

public class ActUser extends AppCompatActivity implements View.OnClickListener {

    private ISmartCityApi smartCityApi;
    private SharedPreferences sPref;
    private User user;
    private EditText editName, editSurname, editSubname, editDate, editEmail;
    private Button buttonLogout;
    private ProgressBar progressBar;

    @SuppressLint("LongLogTag")
    private String loadToken() {
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        String access_token = sPref.getString("access_token", "");
        Log.i("ActUser. access_token load", access_token);
        return access_token;
    }

    private void logout(){
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("access_token", "null");
        ed.commit();
    }

    private void loadUser() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        smartCityApi.getUser("Bearer " +loadToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()){
                            user = response.body();
                            editEmail.setText(user.getEmail());
                            editSurname.setText(user.getSurname());
                            editName.setText(user.getName());
                            editSubname.setText(user.getSubname());
                            editDate.setText(user.getDate());
                            Log.i("ActUser. 111", response.body().toString());
                        } else {
                            Log.i("ActUser. 111", response.code() + " "
                                                            + response.errorBody());
                        }
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Intent actLogin = new Intent(ActUser.this, ActLogin.class);
                        startActivity(actLogin);
                        Log.e("ActUser. 111", t.toString());
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_user);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editName = (EditText) findViewById(R.id.editName);
        editSurname = (EditText) findViewById(R.id.editSurname);
        editSubname = (EditText) findViewById(R.id.editSubname);
        editDate = (EditText) findViewById(R.id.editDate);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.ActUser_progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogout:
                logout();
                finish();
                break;
        }
    }
}
