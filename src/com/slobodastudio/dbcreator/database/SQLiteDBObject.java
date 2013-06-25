package com.slobodastudio.dbcreator.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.slobodastudio.dbcreator.database.annotations.CreateField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 14.06.13.
 */

public abstract class SQLiteDBObject {

    /**
     * The method make ContentValues by fields with annotation CreateField and fill their with data of fields.</br>
     * Primary key fields (with type int or long) value of witch equal zero will ignoring in ContentValues.
     */
    public ContentValues getContentValues() throws IllegalAccessException {

        Field[] fields = this.getClass().getDeclaredFields();
        ContentValues values = new ContentValues();
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class)) {
                putFieldValue(values, field);
            }
        }
        return values;
    }

    /**
     * The method insert data from this object.
     * </br>We use ContentValues that create function {@code getContentValues()}.
     */
    public Uri insert(Context context, Uri uri) throws IllegalAccessException {
        ContentResolver cr = context.getContentResolver();
        return cr.insert(uri, getContentValues());
    }

    /**
     * The method insert data from this object using CONTENT_URI of this object.
     * </br>We use ContentValues that create function {@code getContentValues()}.
     */
    public Uri insert(Context context) throws IllegalAccessException, NoSuchFieldException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        ContentResolver cr = context.getContentResolver();
        return cr.insert(uri, getContentValues());
    }

    /**
     * The method update data by Incoming parameters.
     * </br>We use ContentValues that create function {@code getContentValues()}.
     */
    public int update(Context context, Uri uri, String where, String... args) throws IllegalAccessException {
        ContentResolver cr = context.getContentResolver();
        return cr.update(uri, getContentValues(), where, args);
    }

    /**
     * The method update data by Incoming parameters using CONTENT_URI of this object.
     * In this object must be variable with CONTENT_URI.
     * </br>We use ContentValues that create function {@code getContentValues()}.
     */
    public int update(Context context, String where, String... args) throws IllegalAccessException, NoSuchFieldException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        ContentResolver cr = context.getContentResolver();
        return cr.update(uri, getContentValues(), where, args);
    }

    /**
     * The method will update data by primary key of this object.
     * </br>We use ContentValues that create function {@code getContentValues()}.
     */
    public int update(Context context, Uri uri) throws NoSuchFieldException, IllegalAccessException {
        ContentResolver cr = context.getContentResolver();
        Map<String, String[]> where = getWhereExpressionByPK();
        Map.Entry<String, String[]> entryWhere = where.entrySet().iterator().next();
        return cr.update(uri, getContentValues(), entryWhere.getKey(), entryWhere.getValue());
    }

    /**
     * The method update data by Primary Key using CONTENT_URI of this object.
     * In this object must be variable with CONTENT_URI.
     * </br>We use ContentValues that create function {@code getContentValues()}.
     */
    public int update(Context context) throws NoSuchFieldException, IllegalAccessException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        return update(context, uri);
    }

    /**
     * The method return Map with 'where' expression as key and string array with values as whereArgs.
     */
    public Map<String, String[]> getWhereExpressionByPK() throws NoSuchFieldException, IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        String where = "";
        ArrayList<String> args = new ArrayList<String>();
        boolean firstTime = true;
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class) && field.getAnnotation(CreateField.class).PrimaryKey()) {
                field.setAccessible(true);
                if (firstTime) {
                    firstTime = false;
                    where += CreatorOpenHelper.getTableFieldName(field) + "=? ";
                } else {
                    where += "AND " + CreatorOpenHelper.getTableFieldName(field) + "=? ";
                }
                args.add(String.valueOf(field.get(this)));
            }
        }
        Map<String, String[]> whereWithArgs = new HashMap<String, String[]>();
        whereWithArgs.put(where, args.toArray(new String[0]));
        return whereWithArgs;
    }

    /**
     * The method delete data from database by incoming parameters.
     */
    public int delete(Context context, Uri uri, String where, String... args) {
        ContentResolver cr = context.getContentResolver();
        return cr.delete(uri, where, args);
    }

    /**
     * The method delete data by Where Expression using CONTENT_URI of this object.
     * In this object must be variable with CONTENT_URI.
     */
    public int delete(Context context, String where, String... args) throws NoSuchFieldException, IllegalAccessException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        ContentResolver cr = context.getContentResolver();
        return cr.delete(uri, where, args);
    }

    /**
     * The method delete data by Primary Key using CONTENT_URI of this object.
     * In this object must be variable with CONTENT_URI.
     */
    public int delete(Context context) throws NoSuchFieldException, IllegalAccessException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        return delete(context, uri);
    }

    /**
     * The method delete data by Primary Key of this object.
     */
    public int delete(Context context, Uri uri) throws NoSuchFieldException, IllegalAccessException {
        ContentResolver cr = context.getContentResolver();
        Map<String, String[]> where = getWhereExpressionByPK();
        Map.Entry<String, String[]> entryWhere = where.entrySet().iterator().next();
        return cr.delete(uri, entryWhere.getKey(), entryWhere.getValue());
    }

    private ContentValues putFieldValue(ContentValues values, Field field) throws IllegalAccessException {

        field.setAccessible(true);
        if (field.getType() == int.class) {
            if (!field.getAnnotation(CreateField.class).PrimaryKey() || field.getInt(this) != 0)
                values.put(CreatorOpenHelper.getTableFieldName(field).toString(), field.getInt(this));
        } else if (field.getType() == long.class) {
            if (!field.getAnnotation(CreateField.class).PrimaryKey() || field.getLong(this) != 0)
                values.put(CreatorOpenHelper.getTableFieldName(field).toString(), field.getLong(this));
        } else if (field.getType() == float.class) {
            values.put(CreatorOpenHelper.getTableFieldName(field).toString(), field.getFloat(this));
        } else if (field.getType() == double.class) {
            values.put(CreatorOpenHelper.getTableFieldName(field).toString(), field.getDouble(this));
        } else if (field.getType() == boolean.class) {
            values.put(CreatorOpenHelper.getTableFieldName(field).toString(), field.getBoolean(this) ? 1 : 0);
        } else {
            values.put(CreatorOpenHelper.getTableFieldName(field).toString(), (String) field.get(this));
        }
        return values;
    }

    private void setValueToField(Cursor cursor, Field field) throws IllegalAccessException {

        field.setAccessible(true);
        if (field.getType() == int.class) {
            field.setInt(this, cursor.getInt(cursor.getColumnIndexOrThrow(String.valueOf(CreatorOpenHelper.getTableFieldName(field)))));
        } else if (field.getType() == long.class) {
            field.setLong(this, cursor.getLong(cursor.getColumnIndexOrThrow(String.valueOf(CreatorOpenHelper.getTableFieldName(field)))));
        } else if (field.getType() == float.class) {
            field.setFloat(this, cursor.getFloat(cursor.getColumnIndexOrThrow(String.valueOf(CreatorOpenHelper.getTableFieldName(field)))));
        } else if (field.getType() == double.class) {
            field.setDouble(this, cursor.getDouble(cursor.getColumnIndexOrThrow(String.valueOf(CreatorOpenHelper.getTableFieldName(field)))));
        } else if (field.getType() == boolean.class) {
            field.setBoolean(this, cursor.getInt(cursor.getColumnIndexOrThrow(String.valueOf(CreatorOpenHelper.getTableFieldName(field)))) == 1);
        } else {
            field.set(this, cursor.getString(cursor.getColumnIndexOrThrow(String.valueOf(CreatorOpenHelper.getTableFieldName(field)))));
        }

    }

    /**
     * The method make query to DB
     *
     * @param order - order.
     * @param args  - where arguments.
     */
    public Cursor query(Context context, Uri uri, String projection[], String where, String args[], String order) throws IllegalAccessException {
        ContentResolver cr = context.getContentResolver();
        return cr.query(uri, projection, where, args, order);
    }

    /**
     * The method make query by CONTENT_URI, that created in this object. In this object must be variable with CONTENT_URI.
     *
     * @param order - order.
     * @param args  - where arguments.
     */
    public Cursor query(Context context, String projection[], String where, String args[], String order) throws IllegalAccessException, NoSuchFieldException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        ContentResolver cr = context.getContentResolver();
        return cr.query(uri, projection, where, args, order);
    }

    /**
     * The function fill fields of this object from data that was read from cursor.
     */
    public void fillFields(Cursor cursor) throws IllegalAccessException, NoSuchFieldException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class)) {
                setValueToField(cursor, field);
            }
        }
    }

    /**
     * The method make query to database by incoming parameters, then move cursor to first, then fill fields.
     */
    public void queryForFirst(Context context, Uri uri, String where, String args[], String order) throws IllegalAccessException, NoSuchFieldException {

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, where, args, order);
        if (cursor != null && cursor.moveToFirst()) {
            fillFields(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /**
     * The method make query to database by incoming parameters, then move cursor to first, then fill fields of this object.
     * In this object must be variable with CONTENT_URI.
     */
    public void queryForFirst(Context context, String where, String args[], String order) throws IllegalAccessException, NoSuchFieldException {
        Uri uri = (Uri) this.getClass().getField("CONTENT_URI").get(this);
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, where, args, order);
        if (cursor != null && cursor.moveToFirst()) {
            fillFields(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
