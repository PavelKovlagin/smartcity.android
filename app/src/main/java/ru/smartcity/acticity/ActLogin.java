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

public class ActLogin extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmail, editPassword;
    private Button buttonLogin, buttonActRegister;
    private ISmartCityApi smartCityApi;
    private SharedPreferences sPref;
    private ProgressBar progressBar;
    private Intent actUser;

    @SuppressLint("LongLogTag")
    private void saveToken(String access_token) {
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("access_token", access_token);
        Log.i("ActLogin. access_token save", access_token);
        ed.commit();
    }

    private void getToken() {
        buttonLogin.setEnabled(false);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", editEmail.getText().toString());
        params.put("password", editPassword.getText().toString());
        params.put("grant_type", getString(R.string.grant_type));
        params.put("client_id", getString(R.string.client_id));
        params.put("client_secret", getString(R.string.client_secret));
        smartCityApi.getToken(params)
                .enqueue(new Callback<Map<String,String>>() {
                    @Override
                    public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                        if (response.isSuccessful()){
                            String access_token = String.valueOf(response.body().get("access_token"));
                            saveToken(access_token);
                            startActivity(actUser);
                            finish();
                            Log.i("ActLogin. Response", access_token);
                        } else {
                            Toast.makeText(ActLogin.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                            Log.i("ActLogin. Response", String.valueOf(response.code() + " "
                                    + String.valueOf(response.errorBody())));
                        }
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        buttonLogin.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<Map<String,String>> call, Throwable t) {
                        Toast.makeText(ActLogin.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
                        Log.e("ActLogin. Response", t.toString());
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        buttonLogin.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadToker();
        setContentView(R.layout.activity_act_login);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory .create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);
        actUser = new Intent(this, ActUser.class);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.ActLogin_editPassword);
        buttonLogin = (Button) findViewById(R.id.buttonAuth);
        buttonLogin.setOnClickListener(this);
        buttonActRegister = (Button) findViewById(R.id.ActLogin_buttonActRegister);
        buttonActRegister.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.ActLogin_progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAuth:
                getToken();
                break;
            case R.id.ActLogin_buttonActRegister:
                Intent actRegister = new Intent(this, ActRegister.class);
                finish();
                startActivity(actRegister);
        }

    }
}
