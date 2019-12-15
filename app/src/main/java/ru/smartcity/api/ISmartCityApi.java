package ru.smartcity.api;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.smartcity.models.Event;

import ru.smartcity.models.ServerResponse;
import ru.smartcity.models.User;

public interface ISmartCityApi {
    @GET("/api/events")
    Call<ArrayList<Event>> getEvents(@Query("dateChange") String dateChange);

    @GET("/api/event")
    Call<ArrayList<Event>> getEvent(@Query("event_id") String event_id);

    @FormUrlEncoded
    @POST("/oauth/token")
    Call<Map<String,String>> getToken(@FieldMap  Map<String, String> params);

    @GET("api/user")
    Call<User> getUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("/api/register")
    Call<ServerResponse> register(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("/api/addEvent")
    Call<ServerResponse> addEvent(@Header("Authorization") String authorization, @FieldMap Map<String, String> params);
}
