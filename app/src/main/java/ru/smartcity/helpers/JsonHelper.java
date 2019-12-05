package ru.smartcity.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ru.smartcity.models.Event;

public class JsonHelper {

    static public String getJSONfromEventsList(ArrayList<Event> events) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(events);
    }

    static public ArrayList<Event> getEventsListFromJSON(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Log.i("JSON.ArrayList<Event>", json);
        try {
            Type eventListType = new TypeToken<ArrayList<Event>>() {}.getType();
            ArrayList<Event> events = gson.fromJson(json, eventListType);
            return events;
        } catch (Exception e) {
            return  null;
        }
    }

    static public ArrayList<Event> getEventsFromServer(String adressServer, String linkSelectEventsFromServer, String lastDateUpdate) {
        try {
            HttpHelper httphelper = new HttpHelper();
            httphelper.execute("http://" + adressServer + linkSelectEventsFromServer + "/" + lastDateUpdate);
            String response = httphelper.get();
            ArrayList<Event> events = getEventsListFromJSON(response);
            return events;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return  null;
        }
    }

    static public Event loadEventJsonFromServer(String adressServer, String linkSelectEventFromServer, String event_id) {
        try {
            HttpHelper httphelper = new HttpHelper();
            httphelper.execute("http://" + adressServer + linkSelectEventFromServer + "/" + event_id);
            String response = httphelper.get();
            ArrayList<Event> events = getEventsListFromJSON(response);
            return events.get(0);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return  null;
        }
    }

}
