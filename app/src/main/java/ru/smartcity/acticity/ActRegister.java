package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Date;
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

public class ActRegister extends AppCompatActivity implements View.OnClickListener {

    private EditText editSurname, editName, editSubname, editDate, editEmail, editPassword, editCPassword;
    private ProgressBar progressBar;
    private Button buttonRegister, buttonChangeDate;
    private ISmartCityApi smartCityApi;
    private int DIALOG_DATE = 1;
    private int year, month, day;

    private void register () {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Map<String,String> params = new HashMap<String,String>();
        params.put("surname", editSurname.getText().toString());
        params.put("name", editName.getText().toString());
        params.put("subname", editSubname.getText().toString());
        params.put("date", editDate.getText().toString());
        params.put("email", editEmail.getText().toString());
        params.put("password", editPassword.getText().toString());
        params.put("c_password", editCPassword.getText().toString());

        smartCityApi.register(params).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("ActRegister. 111", response.body().toString());
                } else {
                    Log.e("ActRegister. 111", response.code() + " " + response.errorBody());
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("ActRegister. 111", "failed");
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_register);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = (calendar.get(Calendar.MONTH))+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        Date date = new Date();
//        year = date.getYear();
//        month = date.getMonth();
//        day = date.getDay();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);


        editSurname = (EditText) findViewById(R.id.ActRegister_editSurname);
        editName = (EditText) findViewById(R.id.ActRegister_editName);
        editSubname = (EditText) findViewById(R.id.ActRegister_editSubname);
        editDate = (EditText) findViewById(R.id.ActRegister_editDate);
        editDate.setText(year+"-"+month+"-"+day);
        editEmail = (EditText) findViewById(R.id.ActRegister_editEmail);
        editPassword = (EditText) findViewById(R.id.ActRegister_editPassword);
        editCPassword = (EditText) findViewById(R.id.ActRegister_editCPassword);
        buttonRegister = (Button) findViewById(R.id.ActRegister_buttonRegister);
        buttonRegister.setOnClickListener(this);
        buttonChangeDate = (Button) findViewById(R.id.ActRegister_buttonChangeDate);
        buttonChangeDate.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.ActRegister_progressBar);

    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, year, month, day);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
            year = _year;
            month = _month;
            day = _day;
            editDate.setText(year+"-"+(month+1)+"-"+day);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ActRegister_buttonRegister:
                register();
                break;
            case R.id.ActRegister_buttonChangeDate:
                showDialog(DIALOG_DATE);
                break;
        }
    }
}
