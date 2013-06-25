package com.example.dbcreator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dbcreator.dbmodels.Event;
import com.example.dbcreator.dbmodels.EventType;
import com.slobodastudio.dbcreator.database.CreatorOpenHelper;


/**
 * Created by alex on 17.06.13.
 */
public class DatabaseOpenHelper extends CreatorOpenHelper {

    private static final String DATABASE_NAME = "example.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();

    public DatabaseOpenHelper(Context context) {

        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");//Enable foreign keys
        createTable(db, EventType.class);
        createTable(db, Event.class);
        try {
            EventType type = new EventType();
            type.setName("Birthday");
            db.insert(EventType.TABLE_NAME, null, type.getContentValues());
            type.setName("Meeting");
            db.insert(EventType.TABLE_NAME, null, type.getContentValues());
            type.setName("Buy");
            db.insert(EventType.TABLE_NAME, null, type.getContentValues());
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Error while inserting data", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
