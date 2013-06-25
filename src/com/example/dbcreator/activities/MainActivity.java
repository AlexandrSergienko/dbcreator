package com.example.dbcreator.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.dbcreator.R;
import com.example.dbcreator.database.DatabaseOpenHelper;
import com.example.dbcreator.dbmodels.Event;
import com.example.dbcreator.dbmodels.EventType;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //You can see sql query for creating table.
        Log.d(TAG, DatabaseOpenHelper.getCreateTableScript(Event.class));
        //You can copy constants into your model
        Log.d(TAG, DatabaseOpenHelper.createConstants(Event.class));

        //For inserting data you just need
        try {
            Event event = new Event();
            //Add new event
            event.setEventTime(System.currentTimeMillis());
            event.setUserName("Jon");
            event.setTypeId(1);
            event.insert(getBaseContext());
            //Add one more event
            event.setEventTime(System.currentTimeMillis());
            event.setUserName("Mike");
            event.setTypeId(Math.round(Math.random() * 2) + 1);
            event.insert(getBaseContext());
            //Select first event from database
            event.queryForFirst(getBaseContext(), Event.ID + "=?", new String[]{"1"}, Event.TYPE_ID);
            //Update first event.
            event.setTypeId(2);
            event.update(getBaseContext());
            //Select All events
            Cursor cursor = event.query(getBaseContext(), null, null, null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Event ev = new Event();
                ev.fillFields(cursor);
                EventType evt = new EventType();
                evt.queryForFirst(getBaseContext(), EventType.ID + "=?", new String[]{String.valueOf(ev.getTypeId())}, null);
                Log.d(TAG, evt.getName() + " Event = " + ev.toString());
            }
            cursor.close();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Error to insert data", e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Error to insert data", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
