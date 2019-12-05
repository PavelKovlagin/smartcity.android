package ru.smartcity.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.smartcity.models.Event;

public interface SmartCityApi {
    @GET("/api/events")
    Call<ArrayList<Event>> getEvents(@Query("dateChange") String dateChange);


}
