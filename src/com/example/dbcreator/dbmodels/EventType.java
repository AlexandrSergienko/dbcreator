package com.example.dbcreator.dbmodels;

import android.net.Uri;

import com.example.dbcreator.database.DatabaseProvider;
import com.slobodastudio.dbcreator.database.SQLiteDBObject;
import com.slobodastudio.dbcreator.database.annotations.CreateField;
import com.slobodastudio.dbcreator.database.annotations.CreateTable;


@CreateTable(TableName = "event_types")
public class EventType extends SQLiteDBObject {
    @CreateField(FieldName = "_id", PrimaryKey = true, Autoincrement = true, NotNull = true)
    private long id;
    @CreateField(NotNull = true)
    private String name;

    public static final String TABLE_NAME = "event_types";
    public static final String NAME = "name";
    public static final String ID = "_id";
    public static final Uri CONTENT_URI = DatabaseProvider.BASE_CONTENT_URI.buildUpon()
            .appendPath(TABLE_NAME).build();
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
            + DatabaseProvider.AUTHORITY_CONTENT.replace("com", "vnd") + "." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
            + DatabaseProvider.AUTHORITY_CONTENT.replace("com", "vnd") + "." + TABLE_NAME;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EventType{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
