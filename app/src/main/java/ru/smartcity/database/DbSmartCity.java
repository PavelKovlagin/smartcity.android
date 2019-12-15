package ru.smartcity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ru.smartcity.models.Event;

public class DbSmartCity extends SQLiteOpenHelper {

    private String COLUMN_event_id = "event_id";
    private String COLUMN_eventName = "eventName";
    private String COLUMN_eventDescription = "eventDescription";
    private String COLUMN_latitude = "latitude";
    private String COLUMN_longitude = "longitude";
    private String COLUMN_eventDate = "eventDate";
    private String COLUMN_dateChange = "dateChange";
    private String COLUMN_status_id = "status_id";
    private String COLUMN_statusName = "statusName";
    private String COLUMN_user_id = "user_id";
    private String COLUMN_email = "email";


    public DbSmartCity(Context context) {
        super(context, "smartcity", null, 1);
        Log.i("DbEvents", "create database smartcity");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table events (" +
                COLUMN_event_id + " integer, " +
                COLUMN_eventName + " text," +
                COLUMN_eventDescription + " text, " +
                COLUMN_latitude + " real," +
                COLUMN_longitude + " real," +
                COLUMN_eventDate + " text, " +
                COLUMN_dateChange + " text, " +
                COLUMN_status_id + " integer," +
                COLUMN_statusName + " text, " +
                COLUMN_user_id + " integer, " +
                COLUMN_email + " text" +
                ");");
        Log.i("DbEvents", "create table events");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

    private ContentValues contentValuesEvent(Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_event_id, event.getEvent_id());
        contentValues.put(COLUMN_eventName, event.getEventName());
        contentValues.put(COLUMN_eventDescription, event.getEventDescription());
        contentValues.put(COLUMN_latitude, event.getLatitude());
        contentValues.put(COLUMN_longitude, event.getLongitude());
        contentValues.put(COLUMN_eventDate, event.getEvent_date());
        contentValues.put(COLUMN_dateChange, event.getDateChange());
        contentValues.put(COLUMN_status_id, event.getStatus_id());
        contentValues.put(COLUMN_statusName, event.getStatusName());
        contentValues.put(COLUMN_user_id, event.getUser_id());
        contentValues.put(COLUMN_email, event.getEmail());
        return contentValues;
    }

    private void RudEventSqlite(SQLiteDatabase db, Event event, String RUD) {
        ContentValues cv = contentValuesEvent(event);
        long rowID = 0;
        switch (RUD) {
            case "insert":
                rowID = db.insert("events", null, cv);
                Log.i("SQLite. insert event", "row inserted ID = " + rowID);
                break;
            case "update":
                rowID = db.update("events", cv, "event_id =" + event.getEvent_id(), null);
                Log.i("SQLite. update event", "row update ID = " + rowID);
                break;
            case "delete":
                rowID = db.delete("events", COLUMN_event_id + "=" + event.getEvent_id(), null);
                Log.i("SQLite. delete event", "row deleted ID = " + rowID);
        }
    }

    public void insertOrUpdateEventsFromArrayList(ArrayList<Event> events) {
        SQLiteDatabase db = getWritableDatabase();
        for (Event event : events) {
            Cursor cur = db.rawQuery("SELECT * FROM events WHERE " + COLUMN_event_id + " = " + event.getEvent_id(), null);
            cur.moveToFirst();
            if (cur.getCount() > 0) {
                if (event.getVisibilityForUser() == 0) {
                    RudEventSqlite(db, event, "delete");
                } else {
                    RudEventSqlite(db, event, "update");
                }

            } else {
                if (event.getVisibilityForUser() == 1) RudEventSqlite(db, event, "insert");
            }
            cur.close();
        }
    }

    private Event selectEvent(Cursor cursor) {
        Event event = new Event();
        event.setEvent_id(cursor.getInt(cursor.getColumnIndex(COLUMN_event_id)));
        event.setEventName(cursor.getString(cursor.getColumnIndex(COLUMN_eventName)));
        event.setEventDescription(cursor.getString(cursor.getColumnIndex(COLUMN_eventDescription)));
        event.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_latitude)));
        event.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_longitude)));
        event.setEvent_date(cursor.getString(cursor.getColumnIndex(COLUMN_eventDate)));
        event.setDateChange(cursor.getString(cursor.getColumnIndex(COLUMN_dateChange)));
        event.setStatus_id(cursor.getInt(cursor.getColumnIndex(COLUMN_status_id)));
        event.setStatusName(cursor.getString(cursor.getColumnIndex(COLUMN_statusName)));
        event.setUser_id(cursor.getInt(cursor.getColumnIndex(COLUMN_user_id)));
        event.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_email)));
        Log.i("SQLite.selectEvent", event.toString());
        return event;
    }

    public Event getEventFromSQL(String event_id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE " + COLUMN_event_id + "=" + event_id, null);
        cursor.moveToFirst();
        return selectEvent(cursor);
    }

    public ArrayList<Event> getEventsArrayList() {
        ArrayList<Event> events = new ArrayList<Event>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from events", null);
        //Cursor cursor = db.query("event", null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                events.add(selectEvent(cursor));
            }
        } finally {
            cursor.close();
        }
        return events;
    }
}
