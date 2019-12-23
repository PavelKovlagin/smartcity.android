package ru.smartcity.acticity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
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
    private TextView textValidator;
    private ProgressBar progressBar;
    private Button buttonRegister, buttonChangeDate;
    private ISmartCityApi smartCityApi;
    private int DIALOG_DATE = 1;
    private int year, month, day;
    private String emailPattern = "\\S+@\\S+\\.\\S+";
    private boolean emailValidator = false, passwordValidator = false;
    private SharedPreferences sPref;
    private Intent actUser;

    @SuppressLint("LongLogTag")
    private void saveToken(String access_token) {
        sPref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("access_token", access_token);
        Log.i("ActRegister. access_token save", access_token);
        ed.commit();
    }

    private void validate(){
        StringBuffer sb = new StringBuffer();
        if(!emailValidator) sb.append(getString(R.string.wrongEmail));
        if(!passwordValidator) sb.append("\n" + getString(R.string.passwordsDoNotMatch));
        textValidator.setText(sb);
    }

    private void register () {
        buttonRegister.setEnabled(false);
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
                    String access_token = response.body().getData().get("token");
                    saveToken(access_token);
                    finish();
                    startActivity(actUser);
                    Log.i("ActRegister. Response", response.body().toString());
                } else {
                    Toast.makeText(ActRegister.this, getString(R.string.falseRegister), Toast.LENGTH_SHORT).show();
                    Log.e("ActRegister. Response", response.code() + " " + response.errorBody());
                }
                buttonRegister.setEnabled(true);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("ActRegister. Response", getString(R.string.falseServer));
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                buttonRegister.setEnabled(true);
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

        actUser = new Intent(this, ActUser.class);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.smartCityAdress))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        smartCityApi = retrofit.create(ISmartCityApi.class);
        textValidator = (TextView) findViewById(R.id.ActRegister_textValidator);
        editSurname = (EditText) findViewById(R.id.ActRegister_editSurname);
        editName = (EditText) findViewById(R.id.ActRegister_editName);
        editSubname = (EditText) findViewById(R.id.ActRegister_editSubname);
        editDate = (EditText) findViewById(R.id.ActRegister_editDate);
        editDate.setText(year+"-"+month+"-"+day);
        editEmail = (EditText) findViewById(R.id.ActRegister_editEmail);
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editEmail.getText().toString().matches(emailPattern)) {
                    editEmail.setTextColor(Color.RED);
                    emailValidator = false;
                } else {
                    editEmail.setTextColor(Color.BLACK);
                    emailValidator = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editPassword = (EditText) findViewById(R.id.ActRegister_editPassword);
        editCPassword = (EditText) findViewById(R.id.ActRegister_editCPassword);
        editCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editPassword.getText().toString().equals(editCPassword.getText().toString())) {
                    editCPassword.setTextColor(Color.RED);
                    passwordValidator = false;
                } else {
                    editCPassword.setTextColor(Color.BLACK);
                    passwordValidator = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                if (emailValidator && passwordValidator) {
                    register();
                } else {
                    validate();
                }
                break;
            case R.id.ActRegister_buttonChangeDate:
                showDialog(DIALOG_DATE);
                break;
        }
    }
}
