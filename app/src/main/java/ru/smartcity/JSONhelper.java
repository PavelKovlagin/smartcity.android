package ru.smartcity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class JSONhelper {

    static public String getJSONfromEventList(ArrayList<Event> events) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(events);
    }

    static public ArrayList<Event> getEventListFromJSON(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type eventListType = new TypeToken<ArrayList<Event>>(){}.getType();
        ArrayList<Event> events = gson.fromJson(json, eventListType);
        return events;
    }

    static public ArrayList<Event> getEventsFromServer() {
        try {
            HTTPhelper httphelper = new HTTPhelper();
            httphelper.execute("http://192.168.1.3:8000/api/events");
            String response = httphelper.get();
            ArrayList<Event> events = getEventListFromJSON(response);

            return events;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return  null;
        }
    }

}
