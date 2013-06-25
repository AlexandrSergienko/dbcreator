package com.example.dbcreator.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.dbcreator.dbmodels.Event;
import com.example.dbcreator.dbmodels.EventType;
import com.slobodastudio.dbcreator.database.SelectionBuilder;


public class DatabaseProvider extends ContentProvider {

    public static final String AUTHORITY_CONTENT = "com.example.dbcreator";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY_CONTENT);
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int EVENT_TYPE_DIR_ID = 10;
    private static final int EVENT_TYPE_ITEM_ID = 15;
    private static final int EVENT_DIR_ID = 20;
    private static final int EVENT_ITEM_ID = 25;

    private static final String TAG = DatabaseProvider.class.getSimpleName();
    // Deal with OnCreate call back
    private DatabaseOpenHelper mDatabaseOpenHelper;

    private static UriMatcher buildUriMatcher() {

        String authority = AUTHORITY_CONTENT;
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(authority, EventType.TABLE_NAME, EVENT_TYPE_DIR_ID);
        matcher.addURI(authority, EventType.TABLE_NAME + "/#", EVENT_TYPE_ITEM_ID);


        matcher.addURI(authority, Event.TABLE_NAME, EVENT_DIR_ID);
        matcher.addURI(authority, Event.TABLE_NAME + "/#", EVENT_ITEM_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db;
        try {
            db = mDatabaseOpenHelper.getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException e) {
            Log.e(TAG, "Error to open database. Recreate connection.");
            mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
            db = mDatabaseOpenHelper.getWritableDatabase();
        }
        final Cursor cur = getSimpleSelectionBuilder(uri, selection, selectionArgs).query(db, projection,
                sortOrder);
        // Tell the cursor what uri to watch,
        // so it knows when its source data changes
        cur.setNotificationUri(getContext().getContentResolver(), uri);
        return cur;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case EVENT_TYPE_DIR_ID:
                return EventType.CONTENT_TYPE;
            case EVENT_DIR_ID:
                return Event.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db;
        try {
            db = mDatabaseOpenHelper.getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException e) {
            Log.e(TAG, "Error to open database. Recreate connection.");
            mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
            db = mDatabaseOpenHelper.getWritableDatabase();
        }
        Uri insertedUri = null;
        long insertedId;
        switch (sUriMatcher.match(uri)) {
            case EVENT_TYPE_DIR_ID:
                insertedId = db.insertOrThrow(EventType.TABLE_NAME, null, values);
                insertedUri = ContentUris.withAppendedId(EventType.CONTENT_URI, insertedId);
                break;
            case EVENT_DIR_ID:
                insertedId = db.insertOrThrow(Event.TABLE_NAME, null, values);
                insertedUri = ContentUris.withAppendedId(Event.CONTENT_URI, insertedId);
                break;
        }
        getContext().getContentResolver().notifyChange(insertedUri, null);
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db;
        try {
            db = mDatabaseOpenHelper.getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException e) {
            Log.e(TAG, "Error to open database. Recreate connection.");
            mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
            db = mDatabaseOpenHelper.getWritableDatabase();
        }
        int count = getSimpleSelectionBuilder(uri, selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private SelectionBuilder getSimpleSelectionBuilder(Uri uri, String selection, String[] selectionArgs) {

        SelectionBuilder builder = new SelectionBuilder();
        String id;
        switch (sUriMatcher.match(uri)) {
            case EVENT_TYPE_DIR_ID:
                builder.table(EventType.TABLE_NAME);
                if (TextUtils.isEmpty(selection)) {
                    builder.where(null, (String[]) null);
                } else {
                    builder.where(selection, selectionArgs);
                }
                break;
            case EVENT_TYPE_ITEM_ID:
                id = uri.getPathSegments().get(1);
                builder.table(EventType.TABLE_NAME).where(EventType.ID + "=?", id);
                break;

            case EVENT_DIR_ID:
                builder.table(Event.TABLE_NAME);
                if (TextUtils.isEmpty(selection)) {
                    builder.where(null, (String[]) null);
                } else {
                    builder.where(selection, selectionArgs);
                }
                break;
            case EVENT_ITEM_ID:
                id = uri.getPathSegments().get(1);
                builder.table(Event.TABLE_NAME).where(Event.ID + "=?", id);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        return builder;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case EVENT_TYPE_DIR_ID:
                if (TextUtils.isEmpty(selection))
                    throw new IllegalArgumentException(
                            "You are trying update location dir without specify selection. No sence.");
                break;
            case EVENT_TYPE_ITEM_ID:

                break;

            case EVENT_DIR_ID:
                if (TextUtils.isEmpty(selection))
                    throw new IllegalArgumentException(
                            "You are trying update location dir without specify selection. No sence.");
                break;
            case EVENT_ITEM_ID:

                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        SQLiteDatabase db;
        try {
            db = mDatabaseOpenHelper.getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException e) {
            Log.e(TAG, "Error to open database. Recreate connection.");
            mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
            db = mDatabaseOpenHelper.getWritableDatabase();
        }
        int count = getSimpleSelectionBuilder(uri, selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
